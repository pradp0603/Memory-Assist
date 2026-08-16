import React, {
  useCallback,
  useState,
} from "react";

import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

import {
  router,
  useFocusEffect,
} from "expo-router";

import {
  SafeAreaView,
} from "react-native-safe-area-context";

import {
  registerUser,
} from "../services/api";

export default function RegisterScreen() {
  const [fullName, setFullName] =
    useState("");

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [showPassword, setShowPassword] =
    useState(false);

  const [age, setAge] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

    useFocusEffect(
    useCallback(() => {
        setFullName("");
        setEmail("");
        setAge("");
        setPassword("");
        setShowPassword(false);
        setError("");
    }, [])
    );

  const register = async () => {
    const cleanName =
      fullName.trim();

    const cleanEmail =
      email.trim();

    const cleanAge =
      age.trim();

    if (
      !cleanName ||
      !cleanEmail ||
      !password ||
      !cleanAge
    ) {
      setError(
        "Please complete all fields."
      );

      return;
    }

    const numericAge =
      Number(cleanAge);

    if (
      !Number.isInteger(numericAge) ||
      numericAge <= 0
    ) {
      setError(
        "Please enter a valid age."
      );

      return;
    }

    if (loading) {
      return;
    }

    setLoading(true);
    setError("");

    try {
      await registerUser({
        fullName: cleanName,
        email: cleanEmail,
        password,
        age: numericAge,
      });

      /*
       * Password is never stored.
       */
      setPassword("");

      /*
       * After successful registration,
       * return to Login.
       */
      router.replace({
        pathname: "/login",
        params: {
          registered: "true",
        },
      });
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Unable to create your account. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView
      style={styles.container}
      edges={[
        "top",
        "bottom",
      ]}
    >
      <KeyboardAvoidingView
        style={styles.container}
        behavior={
          Platform.OS === "ios"
            ? "padding"
            : "height"
        }
      >
        <ScrollView
          contentContainerStyle={
            styles.content
          }
          keyboardShouldPersistTaps="handled"
        >
          {/* Header */}

          <View style={styles.brand}>
            <View
              style={styles.logoBox}
            >
              <Text style={styles.logo}>
                🧠
              </Text>

              <View
                style={styles.accentDot}
              />
            </View>

            <Text
              style={styles.appTitle}
            >
              Alzheimer's Assistant
            </Text>

            <Text
              style={styles.subtitle}
            >
              Create your account to keep
              your memories, contacts and
              reminders personal.
            </Text>
          </View>

          {/* Registration Card */}

          <View style={styles.card}>
            <Text style={styles.title}>
              Create Account
            </Text>

            <Text
              style={
                styles.description
              }
            >
              Enter your information below.
            </Text>

            {/* Full Name */}

            <Text style={styles.label}>
              Full Name
            </Text>

            <TextInput
              style={styles.input}
              placeholder="Example: Nick"
              placeholderTextColor="#9CA3AF"
              value={fullName}
              onChangeText={setFullName}
              editable={!loading}
              autoCapitalize="words"
              accessibilityLabel="Full name"
            />

            {/* Email */}

            <Text style={styles.label}>
              Email
            </Text>

            <TextInput
              style={styles.input}
              placeholder="nick@gmail.com"
              placeholderTextColor="#9CA3AF"
              value={email}
              onChangeText={setEmail}
              editable={!loading}
              keyboardType="email-address"
              autoCapitalize="none"
              autoCorrect={false}
              accessibilityLabel="Email"
            />

            {/* Age */}

            <Text style={styles.label}>
              Age
            </Text>

            <TextInput
              style={styles.input}
              placeholder="Example: 79"
              placeholderTextColor="#9CA3AF"
              value={age}
              onChangeText={setAge}
              editable={!loading}
              keyboardType="number-pad"
              accessibilityLabel="Age"
            />

            {/* Password */}

            <Text style={styles.label}>
              Password
            </Text>

            <View
              style={
                styles.passwordContainer
              }
            >
              <TextInput
                style={
                  styles.passwordInput
                }
                placeholder="Create a password"
                placeholderTextColor="#9CA3AF"
                value={password}
                onChangeText={setPassword}
                secureTextEntry={
                  !showPassword
                }
                editable={!loading}
                autoCapitalize="none"
                autoCorrect={false}
                accessibilityLabel="Password"
                onSubmitEditing={register}
              />

              <TouchableOpacity
                style={
                  styles.passwordToggle
                }
                onPress={() =>
                  setShowPassword(
                    (previous) =>
                      !previous
                  )
                }
                disabled={loading}
                activeOpacity={0.7}
                accessibilityRole="button"
                accessibilityLabel={
                  showPassword
                    ? "Hide password"
                    : "Show password"
                }
              >
                <Text
                  style={
                    styles.passwordToggleText
                  }
                >
                  {showPassword
                    ? "Hide"
                    : "Show"}
                </Text>
              </TouchableOpacity>
            </View>

            {/* Error */}

            {error !== "" && (
              <View
                style={
                  styles.errorBox
                }
              >
                <Text
                  style={
                    styles.errorText
                  }
                >
                  {error}
                </Text>
              </View>
            )}

            {/* Create Account */}

            <TouchableOpacity
              style={[
                styles.registerButton,

                loading &&
                  styles.disabledButton,
              ]}
              onPress={register}
              disabled={loading}
              activeOpacity={0.85}
              accessibilityRole="button"
              accessibilityLabel="Create account"
            >
              {loading ? (
                <ActivityIndicator
                  color="#FFFFFF"
                />
              ) : (
                <Text
                  style={
                    styles.registerButtonText
                  }
                >
                  Create Account
                </Text>
              )}
            </TouchableOpacity>

            {/* Back to Login */}

            <View
              style={styles.loginRow}
            >
              <Text
                style={styles.loginText}
              >
                Already have an account?
              </Text>

              <TouchableOpacity
                onPress={() =>
                  router.replace(
                    "/login"
                  )
                }
                disabled={loading}
              >
                <Text
                  style={
                    styles.loginLink
                  }
                >
                  Sign In
                </Text>
              </TouchableOpacity>
            </View>

            {/* Security Message */}

            <View
              style={
                styles.securityBox
              }
            >
              <Text
                style={
                  styles.securityIcon
                }
              >
                🔒
              </Text>

              <Text
                style={
                  styles.securityText
                }
              >
                Your password is sent to the
                server and is never stored by
                the app.
              </Text>
            </View>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles =
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: "#F8F7FF",
    },

    content: {
      flexGrow: 1,
      justifyContent: "center",
      paddingHorizontal: 22,
      paddingVertical: 30,
    },

    brand: {
      alignItems: "center",
      marginBottom: 24,
    },

    logoBox: {
      width: 76,
      height: 76,
      borderRadius: 24,
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: "#EEEAFE",
      position: "relative",
    },

    logo: {
      fontSize: 39,
    },

    accentDot: {
      position: "absolute",
      right: 7,
      bottom: 7,
      width: 13,
      height: 13,
      borderRadius: 7,
      backgroundColor: "#14B8A6",
      borderWidth: 2,
      borderColor: "#FFFFFF",
    },

    appTitle: {
      marginTop: 15,
      fontSize: 27,
      fontWeight: "800",
      color: "#1F2937",
      textAlign: "center",
    },

    subtitle: {
      marginTop: 8,
      maxWidth: 330,
      fontSize: 15,
      lineHeight: 22,
      color: "#6B7280",
      textAlign: "center",
    },

    card: {
      padding: 22,
      borderRadius: 20,
      borderWidth: 1,
      borderColor: "#E8E7F0",
      backgroundColor: "#FFFFFF",
    },

    title: {
      fontSize: 23,
      fontWeight: "700",
      color: "#1F2937",
    },

    description: {
      marginTop: 4,
      marginBottom: 22,
      fontSize: 15,
      color: "#6B7280",
    },

    label: {
      marginBottom: 7,
      fontSize: 15,
      fontWeight: "600",
      color: "#374151",
    },

    input: {
      minHeight: 52,
      marginBottom: 15,
      paddingHorizontal: 15,
      borderWidth: 1,
      borderColor: "#D9D7E5",
      borderRadius: 12,
      backgroundColor: "#FAFAFD",
      fontSize: 16,
      color: "#111827",
    },

    /*
     * Password field
     */

    passwordContainer: {
      flexDirection: "row",
      alignItems: "center",
      minHeight: 52,
      marginBottom: 15,
      borderWidth: 1,
      borderColor: "#D9D7E5",
      borderRadius: 12,
      backgroundColor: "#FAFAFD",
    },

    passwordInput: {
      flex: 1,
      minHeight: 52,
      paddingHorizontal: 15,
      fontSize: 16,
      color: "#111827",
    },

    passwordToggle: {
      minHeight: 52,
      justifyContent: "center",
      alignItems: "center",
      paddingHorizontal: 15,
    },

    passwordToggleText: {
      fontSize: 14,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    errorBox: {
      padding: 12,
      marginBottom: 15,
      borderRadius: 10,
      backgroundColor: "#FEF2F2",
    },

    errorText: {
      fontSize: 14,
      lineHeight: 20,
      color: "#B91C1C",
    },

    registerButton: {
      minHeight: 54,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 12,
      backgroundColor: "#6C5CE7",
    },

    disabledButton: {
      opacity: 0.6,
    },

    registerButtonText: {
      fontSize: 17,
      fontWeight: "700",
      color: "#FFFFFF",
    },

    loginRow: {
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      marginTop: 20,
    },

    loginText: {
      fontSize: 14,
      color: "#6B7280",
    },

    loginLink: {
      marginLeft: 5,
      fontSize: 14,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    securityBox: {
      flexDirection: "row",
      alignItems: "flex-start",
      marginTop: 18,
      padding: 12,
      borderRadius: 11,
      backgroundColor: "#EAFBF8",
    },

    securityIcon: {
      marginRight: 8,
      fontSize: 14,
    },

    securityText: {
      flex: 1,
      fontSize: 12,
      lineHeight: 18,
      color: "#0F766E",
    },
  });