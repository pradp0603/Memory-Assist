import React, {
  useState,
} from "react";

import {
  ActivityIndicator,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

import {
  SafeAreaView,
} from "react-native-safe-area-context";

import {
  sendChatMessage,
} from "../services/api";

export default function RemindersScreen() {
  const [response, setResponse] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const sendRequest = async (
    message: string
  ) => {
    if (loading) {
      return;
    }

    setLoading(true);
    setResponse("");

    try {
      const result =
        await sendChatMessage({
          message,
        });

      setResponse(
        result.response ||
          "No reminders were found."
      );
    } catch (error) {
      setResponse(
        error instanceof Error
          ? error.message
          : "Something went wrong. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const getTodayReminders =
    async () => {
      await sendRequest(
        "What are my reminders today?"
      );
    };

  const getTomorrowReminders =
    async () => {
      await sendRequest(
        "What are my reminders tomorrow?"
      );
    };

  return (
    <SafeAreaView
      style={styles.container}
      edges={["top"]}
    >
      <ScrollView
        contentContainerStyle={
          styles.content
        }
      >
        <View style={styles.header}>
          <View
            style={styles.iconBox}
          >
            <Text style={styles.icon}>
              ⏰
            </Text>
          </View>

          <Text style={styles.title}>
            My Reminders
          </Text>

          <Text style={styles.subtitle}>
            View your upcoming reminders.
          </Text>
        </View>

        <View
          style={
            styles.quickSection
          }
        >
          <TouchableOpacity
            style={[
              styles.quickButton,
              styles.todayButton,
            ]}
            onPress={
              getTodayReminders
            }
            disabled={loading}
          >
            <Text
              style={
                styles.quickIcon
              }
            >
              📅
            </Text>

            <Text
              style={
                styles.quickTitle
              }
            >
              Today's Reminders
            </Text>

            <Text
              style={
                styles.quickDescription
              }
            >
              See what you need to do
              today.
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.quickButton,
              styles.tomorrowButton,
            ]}
            onPress={
              getTomorrowReminders
            }
            disabled={loading}
          >
            <Text
              style={
                styles.quickIcon
              }
            >
              🌅
            </Text>

            <Text
              style={
                styles.quickTitle
              }
            >
              Tomorrow's Reminders
            </Text>

            <Text
              style={
                styles.quickDescription
              }
            >
              See what's planned for
              tomorrow.
            </Text>
          </TouchableOpacity>
        </View>

        {loading && (
          <View
            style={
              styles.loadingCard
            }
          >
            <ActivityIndicator
              color="#6C5CE7"
            />

            <Text
              style={
                styles.loadingText
              }
            >
              Loading reminders...
            </Text>
          </View>
        )}

        {response !== "" &&
          !loading && (
            <View
              style={
                styles.responseCard
              }
            >
              <Text
                style={
                  styles.responseTitle
                }
              >
                🧠 Assistant
              </Text>

              <Text
                style={
                  styles.responseText
                }
              >
                {response}
              </Text>
            </View>
          )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles =
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: "#F7F7FC",
    },

    content: {
      flexGrow: 1,
      padding: 20,
      paddingBottom: 40,
    },

    header: {
      alignItems: "center",
      marginTop: 15,
      marginBottom: 30,
    },

    iconBox: {
      width: 68,
      height: 68,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 22,
      backgroundColor: "#EEEAFE",
    },

    icon: {
      fontSize: 34,
    },

    title: {
      marginTop: 12,
      fontSize: 28,
      fontWeight: "700",
      color: "#1F2937",
    },

    subtitle: {
      marginTop: 8,
      fontSize: 15,
      color: "#6B7280",
    },

    quickSection: {
      gap: 16,
    },

    quickButton: {
      minHeight: 145,
      justifyContent: "center",
      alignItems: "center",
      padding: 20,
      borderRadius: 18,
      borderWidth: 1,
      backgroundColor: "#FFFFFF",
    },

    todayButton: {
      borderColor: "#DCD7FF",
      backgroundColor: "#FAF9FF",
    },

    tomorrowButton: {
      borderColor: "#CDEFE9",
      backgroundColor: "#F7FFFD",
    },

    quickIcon: {
      fontSize: 35,
    },

    quickTitle: {
      marginTop: 10,
      fontSize: 19,
      fontWeight: "700",
      color: "#1F2937",
    },

    quickDescription: {
      marginTop: 6,
      fontSize: 14,
      lineHeight: 20,
      color: "#6B7280",
      textAlign: "center",
    },

    loadingCard: {
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      marginTop: 18,
      padding: 16,
      borderRadius: 14,
      backgroundColor: "#EEEAFE",
    },

    loadingText: {
      marginLeft: 10,
      fontSize: 15,
      fontWeight: "600",
      color: "#6C5CE7",
    },

    responseCard: {
      marginTop: 18,
      padding: 18,
      borderWidth: 1,
      borderColor: "#E3E1ED",
      borderRadius: 16,
      backgroundColor: "#FFFFFF",
    },

    responseTitle: {
      fontSize: 17,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    responseText: {
      marginTop: 9,
      fontSize: 16,
      lineHeight: 24,
      color: "#374151",
    },
  });