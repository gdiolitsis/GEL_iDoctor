# ============================================================
# GEL iDoctor — R8 / ProGuard Rules
# ============================================================
#
# Policy:
# - Application classes are referenced directly and must remain
#   fully optimizable by R8.
# - Do NOT add package-wide GEL / AndroidX / Material keep rules.
# - Add narrow rules only for code reached indirectly through
#   reflection, JNI, serialization, or other runtime lookup.
# ============================================================


# ============================================================
# METADATA
# ============================================================
# Preserve runtime metadata that libraries / frameworks may inspect.
# These attributes do not keep entire classes or packages alive.
# ============================================================

-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes AnnotationDefault


# ============================================================
# WEBRTC / JNI — TARGETED RULES
# ============================================================
# Native WebRTC performs JNI registration / native-to-Java calls
# that R8 cannot necessarily infer from ordinary Java references.
#
# The bundled WebRTC 144.7559.15 AAR has no consumer R8 rules.
# Preserve only JNI-visible members instead of the whole package.
# ============================================================

# Java -> native JNI downcalls.
# Keep native method names and every type appearing in their descriptors.
-keepclasseswithmembernames,includedescriptorclasses class org.webrtc.** {
    native <methods>;
}

# Native -> Java JNI upcalls generated from WebRTC annotations.
# Methods and constructors are separate because a class does not
# necessarily contain both kinds of annotated member.

-keepclasseswithmembers,includedescriptorclasses class org.webrtc.** {
    @org.webrtc.CalledByNative <methods>;
}

-keepclasseswithmembers,includedescriptorclasses class org.webrtc.** {
    @org.webrtc.CalledByNative <init>(...);
}

-keepclasseswithmembers,includedescriptorclasses class org.webrtc.** {
    @org.webrtc.CalledByNativeUnchecked <methods>;
}

-keepclasseswithmembers,includedescriptorclasses class org.webrtc.** {
    @org.webrtc.CalledByNativeUnchecked <init>(...);
}

# jni_zero is small (17 classes in the current WebRTC artifact).
# Keep it conservatively until/unless the dependency ships its own
# consumer R8 rules.
-keep class org.jni_zero.** { *; }


# ============================================================
# IMPORTANT
# ============================================================
# No blanket rules for:
#
#   com.gel.**
#   androidx.**
#   com.google.android.material.**
#   android.**
#   java.**
#
# Direct code references, Android manifests/resources, and library
# consumer rules are handled by R8 / AGP.
# ============================================================
