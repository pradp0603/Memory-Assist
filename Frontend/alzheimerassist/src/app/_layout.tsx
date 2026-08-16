import React from "react";

import { Tabs } from "expo-router";

import {
  ActivityIndicator,
  StyleSheet,
  Text,
  View,
} from "react-native";

import {
  AuthProvider,
  useAuth,
} from "../context/AuthContext";

function AppTabs() {
  const {
    session,
    isLoading,
  } = useAuth();

  /*
   * While SecureStore is being checked,
   * show a simple loading screen.
   */
  if (isLoading) {
    return (
      <View style={styles.loadingScreen}>
        <View style={styles.loadingLogo}>
          <Text style={styles.loadingEmoji}>
            🧠
          </Text>
        </View>

        <ActivityIndicator
          size="large"
          color="#6C5CE7"
        />

        <Text style={styles.loadingText}>
          Alzheimer's Assistant
        </Text>
      </View>
    );
  }

  const isAuthenticated =
    Boolean(session?.token);

  return (
    <Tabs
      screenOptions={{
        /*
         * We use our own screen headers.
         */
        headerShown: false,

        /*
         * Hide the bottom tab bar when
         * the keyboard opens.
         *
         * This gives the Chat textbox
         * more room on Android.
         */
        tabBarHideOnKeyboard: true,

        /*
         * Tab colors.
         */
        tabBarActiveTintColor:
          "#6C5CE7",

        tabBarInactiveTintColor:
          "#6B7280",

        /*
         * Bottom navigation styling.
         */
        tabBarStyle: {
          height: 67,
          paddingTop: 6,
          paddingBottom: 8,
          backgroundColor: "#FFFFFF",
          borderTopColor: "#E8E7F0",
        },

        tabBarLabelStyle: {
          fontSize: 12,
          fontWeight: "600",
        },
      }}
    >
      {/*
       * Logged-in application.
       *
       * These screens can only be opened
       * when a valid session exists.
       */}

      <Tabs.Protected
        guard={isAuthenticated}
      >
        <Tabs.Screen
          name="index"
          options={{
            title: "Chat",

            tabBarIcon: () => (
              <Text
                style={{
                  fontSize: 21,
                }}
              >
                💬
              </Text>
            ),
          }}
        />

        <Tabs.Screen
          name="memories"
          options={{
            title: "Memories",

            tabBarIcon: () => (
              <Text
                style={{
                  fontSize: 21,
                }}
              >
                🧠
              </Text>
            ),
          }}
        />

        <Tabs.Screen
          name="contacts"
          options={{
            title: "Contacts",

            tabBarIcon: () => (
              <Text
                style={{
                  fontSize: 21,
                }}
              >
                👥
              </Text>
            ),
          }}
        />

        <Tabs.Screen
          name="reminders"
          options={{
            title: "Reminders",

            tabBarIcon: () => (
              <Text
                style={{
                  fontSize: 21,
                }}
              >
                ⏰
              </Text>
            ),
          }}
        />
      </Tabs.Protected>

      {/*
       * Logged-out application.
       *
       * Login and Register are available
       * only when there is no JWT session.
       */}

      <Tabs.Protected
        guard={!isAuthenticated}
      >
        <Tabs.Screen
          name="login"
          options={{
            title: "Login",

            /*
             * Do not show Login as a
             * bottom-navigation tab.
             */
            href: null,

            tabBarStyle: {
              display: "none",
            },
          }}
        />

        <Tabs.Screen
          name="register"
          options={{
            title: "Register",

            /*
             * Do not show Register as a
             * bottom-navigation tab.
             */
            href: null,

            tabBarStyle: {
              display: "none",
            },
          }}
        />
      </Tabs.Protected>
    </Tabs>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <AppTabs />
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  loadingScreen: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F8F7FF",
  },

  loadingLogo: {
    width: 76,
    height: 76,
    borderRadius: 24,
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 22,
    backgroundColor: "#EEEAFE",
  },

  loadingEmoji: {
    fontSize: 38,
  },

  loadingText: {
    marginTop: 15,
    fontSize: 17,
    fontWeight: "600",
    color: "#4B5563",
  },
});