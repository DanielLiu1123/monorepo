package monorepo.lib.msp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

/**
 * @author Freeman
 */
public class ProtobufAccessorNamingStrategy extends DefaultAccessorNamingStrategy {
    /**
     * repeated string getter will return ProtocolStringList
     */
    private static final String PROTOBUF_STRING_LIST_TYPE = "com.google.protobuf.ProtocolStringList";

    private static final String PROTOBUF_MESSAGE_OR_BUILDER = "com.google.protobuf.MessageLiteOrBuilder";
    private static final List<String> INTERNAL_SPECIAL_METHOD_ENDINGS =
            Arrays.asList("Value", "Count", "Bytes", "ValueList");

    private static Set<MethodSignature> INTERNAL_METHODS;

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        INTERNAL_METHODS = getInternalMethods();
    }

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        if (!isProtobufMessage(method.getEnclosingElement())) {
            return super.isGetterMethod(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        if (hasPrefixWithUpperCaseNext(method, "get")) {
            if (!method.getParameters().isEmpty()) {
                return false;
            }
            if (isDeprecated(method)) {
                return false;
            }
            if (isSpecialGetMethod(method)) {
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        if (!isProtobufMessage(method.getEnclosingElement())) {
            return super.isSetterMethod(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        return hasPrefixWithUpperCaseNext(method, "set") || isAddAllMethod(method) || isPutAllMethod(method);
    }

    @Override
    protected boolean isFluentSetter(ExecutableElement method) {
        if (!isProtobufMessage(method.getEnclosingElement())) {
            return super.isFluentSetter(method);
        }

        if (INTERNAL_METHODS.contains(new MethodSignature(method))) {
            return false;
        }

        return hasPrefixWithUpperCaseNext(method, "set") || isAddAllMethod(method) || isPutAllMethod(method);
    }

    @Override
    public boolean isAdderMethod(ExecutableElement method) {
        if (!isProtobufMessage(method.getEnclosingElement())) {
            return super.isAdderMethod(method);
        }

        if (isAddMethod(method)) {
            return true;
        }

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
        if (!isProtobufMessage(getterOrSetterMethod.getEnclosingElement())) {
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
                collectMethods(typeElement, methods);
            }
        }
        return methods;
    }

    private static boolean isSpecialGetMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();

        for (String checkMethod : INTERNAL_SPECIAL_METHOD_ENDINGS) {
            if (methodName.endsWith(checkMethod)) {
                String propertyMethod = methodName.substring(0, methodName.length() - checkMethod.length());
                boolean propertyMethodExists = method.getEnclosingElement().getEnclosedElements().stream()
                        .anyMatch(e -> e.getSimpleName().toString().equals(propertyMethod));
                if (propertyMethodExists) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isAddAllMethod(ExecutableElement method) {
        if (hasPrefixWithUpperCaseNext(method, "addAll")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return isTargetClass(paramType, Iterable.class);
            }
        }
        return false;
    }

    private static boolean isAddMethod(ExecutableElement method) {
        if (hasPrefixWithUpperCaseNext(method, "add")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return !isTargetClass(paramType, Iterable.class); // not addAll
            }
        }
        return false;
    }

    private static boolean isPutAllMethod(ExecutableElement method) {
        if (hasPrefixWithUpperCaseNext(method, "putAll")) {
            if (method.getParameters().size() == 1) {
                TypeMirror paramType = method.getParameters().get(0).asType();
                return isTargetClass(paramType, Map.class);
            }
        }
        return false;
    }

    private static boolean isTargetClass(TypeMirror paramType, Class<?> targetType) {
        return paramType.toString().startsWith(targetType.getCanonicalName());
    }

    private static boolean isGetList(ExecutableElement element) {
        // repeated fields getter: getXxxList()
        return hasPrefixWithUpperCaseNext(element, "get") && isListType(element.getReturnType());
    }

    private static boolean isGetMap(ExecutableElement element) {
        // There are many getter methods for map in protobuf generated code, only one is the real getter:
        // - getXxx deprecated
        // - getMutableXxx deprecated
        // - getXxxMap the real getter
        return !isDeprecated(element)
                && hasPrefixWithUpperCaseNext(element, "get")
                && isMapType(element.getReturnType());
    }

    private static boolean isDeprecated(ExecutableElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    private static boolean isListType(TypeMirror t) {
        return isTargetClass(t, List.class) || t.toString().startsWith(PROTOBUF_STRING_LIST_TYPE);
    }

    private static boolean isMapType(TypeMirror t) {
        return isTargetClass(t, Map.class);
    }

    private static boolean isProtobufMessage(Element type) {
        if (!(type instanceof TypeElement)) {
            return false;
        }

        TypeElement typeElement = (TypeElement) type;
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();

        if (interfaces != null) {
            for (TypeMirror implementedInterface : interfaces) {
                if (implementedInterface.toString().startsWith(PROTOBUF_MESSAGE_OR_BUILDER)) {
                    return true;
                }
                if (implementedInterface instanceof DeclaredType) {
                    DeclaredType declared = (DeclaredType) implementedInterface;
                    Element supertypeElement = declared.asElement();
                    if (isProtobufMessage(supertypeElement)) {
                        return true;
                    }
                }
            }
        }

        TypeMirror superType = typeElement.getSuperclass();
        if (superType instanceof DeclaredType) {
            DeclaredType declared = (DeclaredType) superType;
            Element supertypeElement = declared.asElement();
            return isProtobufMessage(supertypeElement);
        }
        return false;
    }

    private static void collectMethods(TypeElement typeElement, HashSet<MethodSignature> methods) {
        // Collect methods from current type
        for (Element element : typeElement.getEnclosedElements()) {
            if (element instanceof ExecutableElement && isPublicNonStaticMethod((ExecutableElement) element)) {
                methods.add(new MethodSignature((ExecutableElement) element));
            }
        }

        // Collect from superclass
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) superclass;
            Element superElement = declaredType.asElement();
            if (superElement instanceof TypeElement && !superElement.toString().equals("java.lang.Object")) {
                collectMethods((TypeElement) superElement, methods);
            }
        }

        // Collect from interfaces
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (interfaceType instanceof DeclaredType) {
                DeclaredType declaredType = (DeclaredType) interfaceType;
                Element interfaceElement = declaredType.asElement();
                if (interfaceElement instanceof TypeElement) {
                    collectMethods((TypeElement) interfaceElement, methods);
                }
            }
        }
    }

    private static boolean isPublicNonStaticMethod(ExecutableElement method) {
        Set<Modifier> modifiers = method.getModifiers();
        return method.getKind() == ElementKind.METHOD
                && modifiers.contains(Modifier.PUBLIC)
                && !modifiers.contains(Modifier.STATIC);
    }

    private static boolean hasPrefixWithUpperCaseNext(ExecutableElement method, String prefix) {
        String name = method.getSimpleName().toString();
        int len = prefix.length();
        return name.startsWith(prefix) && name.length() > len && Character.isUpperCase(name.charAt(len));
    }

    private static final class MethodSignature {
        private final String name;
        private final List<String> parameterTypes;

        public MethodSignature(ExecutableElement method) {
            this.name = method.getSimpleName().toString();
            this.parameterTypes = method.getParameters().stream()
                    .map(p -> p.asType().toString())
                    .collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MethodSignature that = (MethodSignature) o;
            return Objects.equals(name, that.name) && Objects.equals(parameterTypes, that.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parameterTypes);
        }
    }
}
