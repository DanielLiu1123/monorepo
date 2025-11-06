package monorepo.lib.msp;

/*-
 * #%L
 * protobuf-spi-impl
 * %%
 * Copyright (C) 2019 - 2020 Entur
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Arne Seime
 */
public class ProtobufAccessorNamingStrategy extends DefaultAccessorNamingStrategy {
    /**
     * repeated string
     */
    public static final String PROTOBUF_STRING_LIST_TYPE = "com.google.protobuf.ProtocolStringList";
    public static final String PROTOBUF_MESSAGE_OR_BUILDER = "com.google.protobuf.MessageLiteOrBuilder";

    private static Set<MethodSignature> INTERNAL_METHODS;

    protected static final List<String> INTERNAL_SPECIAL_METHOD_ENDINGS = Arrays.asList("Value", "Count", "Bytes", "ValueList");

    protected TypeMirror protobufMessageOrBuilderType;

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        INTERNAL_METHODS = getInternalMethods();

        TypeElement typeElement = elementUtils.getTypeElement(PROTOBUF_MESSAGE_OR_BUILDER);
        if (typeElement != null) {
            protobufMessageOrBuilderType = typeElement.asType();
        }
    }

    private boolean isSpecialMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();

        for (String checkMethod : INTERNAL_SPECIAL_METHOD_ENDINGS) {
            if (methodName.endsWith(checkMethod)) {
                String propertyMethod = methodName.substring(0, methodName.length() - checkMethod.length());
                boolean propertyMethodExists = method.getEnclosingElement()
                        .getEnclosedElements()
                        .stream()
                        .anyMatch(e -> e.getSimpleName().toString().equals(propertyMethod));
                if (propertyMethodExists) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        if (!isProtobufGeneratedMessage(method.getEnclosingElement())) {
            return super.isGetterMethod(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        String methodName = method.getSimpleName().toString();

        if (methodName.startsWith("get")) {
            if (!method.getParameters().isEmpty()) return false;
            if (isDeprecated(method)) return false;
            if (isGetList(method)) return true;
            if (isGetMap(method)) return true;
            if (isSpecialMethod(method)) return false;
            return true;
        }

        return false;
    }

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        if (!isProtobufGeneratedMessage(method.getEnclosingElement())) {
            return super.isSetterMethod(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        String methodName = method.getSimpleName().toString();

        return methodName.startsWith("set")
               || isAddAllMethod(method)
               || isPutAllMethod(method);
    }

    private boolean isAddAllMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        if (methodName.startsWith("addAll")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return paramType.toString().startsWith(Iterable.class.getCanonicalName());
            }
        }
        return false;
    }

    private boolean isAddMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        if (methodName.startsWith("add")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return !paramType.toString().startsWith(Iterable.class.getCanonicalName());
            }
        }
        return false;
    }

    private boolean isPutAllMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        if (methodName.startsWith("putAll")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return paramType.toString().startsWith(Map.class.getCanonicalName());
            }
        }
        return false;
    }

    @Override
    protected boolean isFluentSetter(ExecutableElement method) {
        if (!isProtobufGeneratedMessage(method.getEnclosingElement())) {
            return super.isFluentSetter(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        String methodName = method.getSimpleName().toString();

        return methodName.startsWith("set")
               || isAddAllMethod(method)
               || isPutAllMethod(method);
    }

    @Override
    public boolean isAdderMethod(ExecutableElement method) {
        if (!isProtobufGeneratedMessage(method.getEnclosingElement())) {
            return super.isAdderMethod(method);
        }

        // Protobuf does not have adder methods, only addAll as a setter for collections
        return false;
    }

    @Override
    public boolean isPresenceCheckMethod(ExecutableElement method) {
        return super.isPresenceCheckMethod(method);
    }

    @Override
    public String getElementName(ExecutableElement adderMethod) {
        return super.getElementName(adderMethod);
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
        if (!isProtobufGeneratedMessage(getterOrSetterMethod.getEnclosingElement())) {
            return super.getPropertyName(getterOrSetterMethod);
        }

        String methodName = getterOrSetterMethod.getSimpleName().toString();

        // 'get...Map'
        if (isGetMap(getterOrSetterMethod)) {
            return IntrospectorUtils.decapitalize(methodName.substring(3, methodName.length() - 3));
        }

        // 'get...List'
        if (isGetList(getterOrSetterMethod)) {
            return IntrospectorUtils.decapitalize(methodName.substring(3, methodName.length() - 4));
        }

        // 'addAll...'
        if (isAddAllMethod(getterOrSetterMethod)) {
            return IntrospectorUtils.decapitalize(methodName.substring(6));
        }

        // 'putAll...'
        if (isPutAllMethod(getterOrSetterMethod)) {
            return IntrospectorUtils.decapitalize(methodName.substring(6));
        }

        return super.getPropertyName(getterOrSetterMethod);
    }

    private boolean isGetList(ExecutableElement element) {
        // repeated fields getter: getXxxList()
        var method = element.getSimpleName().toString();
        return method.startsWith("get")
               && Character.isUpperCase(method.charAt("get".length()))
               && isListType(element.getReturnType());
    }

    private boolean isGetMap(ExecutableElement element) {
        // There are many getter methods for map in protobuf generated code, only one is the real getter:
        // - getXxx deprecated
        // - getMutableXxx deprecated
        // - getXxxMap the real getter
        var method = element.getSimpleName().toString();
        return !isDeprecated(element)
               && method.startsWith("get")
               && Character.isUpperCase(method.charAt("get".length()))
               && isMapType(element.getReturnType());
    }

    private boolean isDeprecated(ExecutableElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    private boolean isListType(TypeMirror t) {
        return t.toString().startsWith(List.class.getCanonicalName()) || t.toString().startsWith(PROTOBUF_STRING_LIST_TYPE);
    }

    private boolean isMapType(TypeMirror t) {
        return t.toString().startsWith(Map.class.getCanonicalName());
    }

    private boolean isProtobufGeneratedMessage(Element type) {
        if (!(type instanceof TypeElement)) {
            return false;
        }

        var typeElement = (TypeElement) type;
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();

        if (interfaces != null) {
            for (TypeMirror implementedInterface : interfaces) {
                if (implementedInterface.toString().startsWith(PROTOBUF_MESSAGE_OR_BUILDER)) {
                    return true;
                } else if (implementedInterface instanceof DeclaredType) {
                    DeclaredType declared = (DeclaredType) implementedInterface;
                    Element supertypeElement = declared.asElement();
                    if (isProtobufGeneratedMessage(supertypeElement)) {
                        return true;
                    }
                }
            }
        }

        TypeMirror superType = typeElement.getSuperclass();
        if (superType instanceof DeclaredType) {
            DeclaredType declared = (DeclaredType) superType;
            Element supertypeElement = declared.asElement();
            return isProtobufGeneratedMessage(supertypeElement);
        }
        return false;
    }

    private Set<MethodSignature> getInternalMethods() {
        String[] internalClasses = {
            "com.google.protobuf.MessageLite",
            "com.google.protobuf.MessageLite.Builder",
            "com.google.protobuf.Message",
            "com.google.protobuf.Message.Builder"
        };

        HashSet<MethodSignature> methods = new HashSet<>();
        for (String className : internalClasses) {
            TypeElement typeElement = elementUtils.getTypeElement(className);
            if (typeElement != null) {
                collectMethodsRecursively(typeElement, methods);
            }
        }
        return methods;
    }

    private void collectMethodsRecursively(TypeElement typeElement, Set<MethodSignature> methods) {
        // Collect methods from current type
        for (Element element : typeElement.getEnclosedElements()) {
            if (element instanceof ExecutableElement) {
                methods.add(new MethodSignature((ExecutableElement) element));
            }
        }

        // Collect from superclass
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) superclass;
            Element superElement = declaredType.asElement();
            if (superElement instanceof TypeElement && !superElement.toString().equals("java.lang.Object")) {
                collectMethodsRecursively((TypeElement) superElement, methods);
            }
        }

        // Collect from interfaces
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (interfaceType instanceof DeclaredType) {
                DeclaredType declaredType = (DeclaredType) interfaceType;
                Element interfaceElement = declaredType.asElement();
                if (interfaceElement instanceof TypeElement) {
                    collectMethodsRecursively((TypeElement) interfaceElement, methods);
                }
            }
        }
    }

    record MethodSignature(String name, List<String> parameterTypes) {
        public MethodSignature(ExecutableElement method) {
            this(method.getSimpleName().toString(),
                    method.getParameters().stream()
                            .map(p -> p.asType().toString())
                            .toList()
            );
        }
    }

}