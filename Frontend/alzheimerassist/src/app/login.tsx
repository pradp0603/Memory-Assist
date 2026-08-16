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
  useAuth,
} from "../context/AuthContext";

export default function LoginScreen() {
  /*
   * We only need signIn and authMessage.
   *
   * clearAuthMessage is NOT needed.
   */
  const {
    signIn,
    authMessage,
  } = useAuth();

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [
    showPassword,
    setShowPassword,
  ] = useState(false);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  /*
   * Clear the Login form whenever
   * the Login screen becomes active.
   *
   * IMPORTANT:
   * We intentionally do NOT clear
   * authMessage here.
   *
   * Otherwise the session-expired
   * message would disappear immediately
   * when Login opens.
   */
  useFocusEffect(
    useCallback(() => {
      setEmail("");
      setPassword("");
      setShowPassword(false);
      setError("");
    }, [])
  );

  const login = async () => {
    const cleanEmail =
      email.trim();

    if (
      !cleanEmail ||
      !password
    ) {
      setError(
        "Please enter your email and password."
      );

      return;
    }

    if (loading) {
      return;
    }

    setLoading(true);
    setError("");

    try {
      await signIn(
        cleanEmail,
        password
      );

      /*
       * Password is not stored.
       */
      setPassword("");

      /*
       * Successful signIn() clears
       * authMessage automatically.
       */
      router.replace("/");
    } catch (error) {
      setError(
        error instanceof Error
          ? error.message
          : "Unable to sign in. Please try again."
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
          showsVerticalScrollIndicator={false}
        >
          {/* Branding */}

          <View style={styles.brand}>
            <View
              style={
                styles.logoContainer
              }
            >
              <Text style={styles.logo}>
                🧠
              </Text>

              <View
                style={
                  styles.accentDot
                }
              />
            </View>

            <Text style={styles.title}>
              Alzheimer's Assistant
            </Text>

            <Text
              style={styles.subtitle}
            >
              Your memories, contacts and
              reminders — safely in one place.
            </Text>
          </View>

          {/* Login Card */}

          <View style={styles.card}>
            <Text
              style={styles.cardTitle}
            >
              Welcome back
            </Text>

            <Text
              style={
                styles.cardSubtitle
              }
            >
              Sign in to continue.
            </Text>

            {/* Session Expired Message */}

            {authMessage !== "" && (
              <View
                style={
                  styles.sessionMessage
                }
              >
                <Text
                  style={
                    styles.sessionMessageText
                  }
                >
                  {authMessage}
                </Text>
              </View>
            )}

            {/* Email */}

            <Text style={styles.label}>
              Email
            </Text>

            <TextInput
              style={styles.input}
              placeholder="akash@gmail.com"
              placeholderTextColor="#9CA3AF"
              value={email}
              onChangeText={setEmail}
              keyboardType="email-address"
              autoCapitalize="none"
              autoCorrect={false}
              editable={!loading}
              accessibilityLabel="Email address"
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
                placeholder="Enter your password"
                placeholderTextColor="#9CA3AF"
                value={password}
                onChangeText={setPassword}
                secureTextEntry={
                  !showPassword
                }
                autoCapitalize="none"
                autoCorrect={false}
                editable={!loading}
                onSubmitEditing={login}
                accessibilityLabel="Password"
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

            {/* Login Error */}

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

            {/* Sign In */}

            <TouchableOpacity
              style={[
                styles.loginButton,

                loading &&
                  styles.disabledButton,
              ]}
              onPress={login}
              disabled={loading}
              activeOpacity={0.85}
              accessibilityRole="button"
              accessibilityLabel="Sign in"
            >
              {loading ? (
                <ActivityIndicator
                  color="#FFFFFF"
                />
              ) : (
                <Text
                  style={
                    styles.loginButtonText
                  }
                >
                  Sign In
                </Text>
              )}
            </TouchableOpacity>

            {/* Registration */}

            <View
              style={
                styles.registerRow
              }
            >
              <Text
                style={
                  styles.registerText
                }
              >
                Don't have an account?
              </Text>

              <TouchableOpacity
                onPress={() =>
                  router.push(
                    "/register"
                  )
                }
                disabled={loading}
              >
                <Text
                  style={
                    styles.registerLink
                  }
                >
                  Create Account
                </Text>
              </TouchableOpacity>
            </View>

            {/* Security Message */}

            <View
              style={
                styles.securityRow
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
                Your password is never stored
                on this device.
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
      marginBottom: 28,
    },

    logoContainer: {
      width: 82,
      height: 82,
      borderRadius: 25,
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: "#EEEAFE",
      position: "relative",
    },

    logo: {
      fontSize: 42,
    },

    accentDot: {
      position: "absolute",
      right: 8,
      bottom: 8,
      width: 13,
      height: 13,
      borderRadius: 7,
      backgroundColor: "#14B8A6",
      borderWidth: 2,
      borderColor: "#FFFFFF",
    },

    title: {
      marginTop: 17,
      fontSize: 28,
      fontWeight: "800",
      color: "#1F2937",
      textAlign: "center",
    },

    subtitle: {
      marginTop: 8,
      maxWidth: 320,
      fontSize: 15,
      lineHeight: 22,
      color: "#6B7280",
      textAlign: "center",
    },

    card: {
      backgroundColor: "#FFFFFF",
      borderRadius: 20,
      padding: 22,
      borderWidth: 1,
      borderColor: "#E8E7F0",
    },

    cardTitle: {
      fontSize: 23,
      fontWeight: "700",
      color: "#1F2937",
    },

    cardSubtitle: {
      marginTop: 4,
      marginBottom: 22,
      fontSize: 15,
      color: "#6B7280",
    },

    /*
     * Session expired notification.
     */
    sessionMessage: {
      marginBottom: 18,
      paddingHorizontal: 14,
      paddingVertical: 12,
      borderRadius: 10,
      borderWidth: 1,
      borderColor: "#DDD6FE",
      backgroundColor: "#F5F3FF",
    },

    sessionMessageText: {
      fontSize: 14,
      lineHeight: 20,
      fontWeight: "600",
      color: "#6C5CE7",
      textAlign: "center",
    },

    label: {
      marginBottom: 7,
      fontSize: 15,
      fontWeight: "600",
      color: "#374151",
    },

    input: {
      minHeight: 52,
      marginBottom: 16,
      paddingHorizontal: 15,
      borderWidth: 1,
      borderColor: "#D9D7E5",
      borderRadius: 12,
      backgroundColor: "#FAFAFD",
      fontSize: 16,
      color: "#111827",
    },

    passwordContainer: {
      flexDirection: "row",
      alignItems: "center",
      minHeight: 52,
      marginBottom: 16,
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

    loginButton: {
      minHeight: 54,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 12,
      backgroundColor: "#6C5CE7",
    },

    disabledButton: {
      opacity: 0.6,
    },

    loginButtonText: {
      fontSize: 17,
      fontWeight: "700",
      color: "#FFFFFF",
    },

    registerRow: {
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      marginTop: 20,
    },

    registerText: {
      fontSize: 14,
      color: "#6B7280",
    },

    registerLink: {
      marginLeft: 5,
      fontSize: 14,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    securityRow: {
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      marginTop: 18,
    },

    securityIcon: {
      marginRight: 7,
      fontSize: 14,
    },

    securityText: {
      fontSize: 12,
      color: "#6B7280",
    },
  });