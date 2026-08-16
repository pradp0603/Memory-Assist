import React, {
  useState,
} from "react";

import {
  ActivityIndicator,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";

import {
  SafeAreaView,
} from "react-native-safe-area-context";

import {
  sendChatMessage,
} from "../services/api";

export default function MemoriesScreen() {
  const [message, setMessage] =
    useState("");

  const [response, setResponse] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const sendMemoryRequest =
    async () => {
      const cleanMessage =
        message.trim();

      if (
        !cleanMessage ||
        loading
      ) {
        return;
      }

      setLoading(true);
      setResponse("");

      try {
        const result =
          await sendChatMessage({
            message: cleanMessage,
          });

        setResponse(
          result.response ||
            "Your request was completed."
        );

        setMessage("");
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

  const clearScreen = () => {
    setMessage("");
    setResponse("");
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
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.header}>
          <View
            style={styles.iconBox}
          >
            <Text style={styles.icon}>
              🧠
            </Text>
          </View>

          <Text style={styles.title}>
            My Memories
          </Text>

          <Text style={styles.subtitle}>
            Save, find or forget important
            information.
          </Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.label}>
            What would you like to say?
          </Text>

          <TextInput
            style={styles.input}
            placeholder="Example: My glasses are in the kitchen drawer"
            placeholderTextColor="#6B7280"
            value={message}
            onChangeText={setMessage}
            multiline
            textAlignVertical="top"
            editable={!loading}
          />

          <TouchableOpacity
            style={[
              styles.sendButton,

              (
                !message.trim() ||
                loading
              ) &&
                styles.disabledButton,
            ]}
            onPress={
              sendMemoryRequest
            }
            disabled={
              !message.trim() ||
              loading
            }
          >
            {loading ? (
              <>
                <ActivityIndicator
                  color="#FFFFFF"
                />

                <Text
                  style={
                    styles.loadingText
                  }
                >
                  Sending...
                </Text>
              </>
            ) : (
              <Text
                style={
                  styles.sendButtonText
                }
              >
                Send
              </Text>
            )}
          </TouchableOpacity>

          {(message !== "" ||
            response !== "") && (
            <TouchableOpacity
              style={
                styles.clearButton
              }
              onPress={clearScreen}
              disabled={loading}
            >
              <Text
                style={
                  styles.clearButtonText
                }
              >
                Clear
              </Text>
            </TouchableOpacity>
          )}
        </View>

        {response !== "" && (
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

        <Text style={styles.helpText}>
          You can save, find or forget a
          memory using a normal sentence.
        </Text>
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
      marginTop: 12,
      marginBottom: 25,
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
      fontSize: 35,
    },

    title: {
      marginTop: 12,
      fontSize: 28,
      fontWeight: "700",
      color: "#111827",
    },

    subtitle: {
      marginTop: 8,
      fontSize: 16,
      color: "#4B5563",
      textAlign: "center",
    },

    card: {
      padding: 18,
      borderRadius: 16,
      borderWidth: 1,
      borderColor: "#E3E1ED",
      backgroundColor: "#FFFFFF",
    },

    label: {
      marginBottom: 10,
      fontSize: 18,
      fontWeight: "600",
      color: "#1F2937",
    },

    input: {
      minHeight: 130,
      padding: 14,
      borderWidth: 1,
      borderColor: "#D1D5DB",
      borderRadius: 12,
      backgroundColor: "#FAFAFD",
      fontSize: 17,
      lineHeight: 25,
      color: "#111827",
    },

    sendButton: {
      flexDirection: "row",
      minHeight: 54,
      marginTop: 15,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 11,
      backgroundColor: "#6C5CE7",
    },

    disabledButton: {
      opacity: 0.5,
    },

    sendButtonText: {
      fontSize: 17,
      fontWeight: "700",
      color: "#FFFFFF",
    },

    loadingText: {
      marginLeft: 8,
      fontSize: 16,
      fontWeight: "600",
      color: "#FFFFFF",
    },

    clearButton: {
      minHeight: 48,
      justifyContent: "center",
      alignItems: "center",
    },

    clearButtonText: {
      fontSize: 16,
      color: "#6B7280",
    },

    responseCard: {
      marginTop: 18,
      padding: 18,
      borderRadius: 16,
      borderWidth: 1,
      borderColor: "#DEDCEC",
      backgroundColor: "#FFFFFF",
    },

    responseTitle: {
      fontSize: 18,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    responseText: {
      marginTop: 10,
      fontSize: 17,
      lineHeight: 26,
      color: "#1F2937",
    },

    helpText: {
      marginTop: 18,
      textAlign: "center",
      fontSize: 14,
      lineHeight: 21,
      color: "#6B7280",
    },
  });