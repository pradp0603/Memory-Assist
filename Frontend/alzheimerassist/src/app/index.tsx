import React, {
  useEffect,
  useRef,
  useState,
} from "react";

import {
  ActivityIndicator,
  Keyboard,
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
  SafeAreaView,
} from "react-native-safe-area-context";

import {
  ExpoSpeechRecognitionModule,
  useSpeechRecognitionEvent,
} from "expo-speech-recognition";

import * as Speech from "expo-speech";

import {
  sendChatMessage,
} from "../services/api";

import {
  useAuth,
} from "../context/AuthContext";

type ChatMessage = {
  id: string;
  sender: "user" | "assistant";
  text: string;
};

export default function ChatScreen() {
  const {
    session,
    signOut,
  } = useAuth();

  const [message, setMessage] =
    useState("");

  const [messages, setMessages] =
    useState<ChatMessage[]>([]);

  const [loading, setLoading] =
    useState(false);

  const [
    recognizing,
    setRecognizing,
  ] = useState(false);

  const voiceSubmittedRef =
    useRef(false);

  const scrollViewRef =
  useRef<ScrollView>(null);

  const scrollToBottom = (
  animated = true
) => {
  requestAnimationFrame(() => {
    scrollViewRef.current?.scrollToEnd({
      animated,
    });
  });
};

  /*
   * Read aloud exactly the same assistant
   * response that is displayed in chat.
   *
   * Speech errors are caught here so they
   * can never break the chat request flow.
   */
  const speakAssistantResponse = async (
    text: string
  ) => {
    const cleanText = text.trim();

    if (!cleanText) {
      return;
    }

    try {
      /*
       * Stop a previous reply before speaking
       * the newest one, so replies do not queue.
       */
      await Speech.stop();

      Speech.speak(cleanText, {
        language: "en-US",
        rate: 0.85,
        pitch: 1.0,
      });
    } catch (error) {
      /*
       * Do not throw from text-to-speech.
       * The response remains visible even if
       * the device cannot speak it.
       */
      console.log(
        "Text-to-speech error:",
        error
      );
    }
  };

  const submitMessage = async (
    text: string
  ) => {
    const cleanMessage =
      text.trim();

    if (
      !cleanMessage ||
      loading
    ) {
      return;
    }

    const userMessage:
      ChatMessage = {
      id: `${Date.now()}-user`,
      sender: "user",
      text: cleanMessage,
    };

    setMessages(
      (previous) => [
        ...previous,
        userMessage,
      ]
    );

    setMessage("");
    setLoading(true);

    try {
      /*
       * No userId.
       *
       * JWT identifies the user.
       */
      const result =
        await sendChatMessage({
          message: cleanMessage,
        });

      const assistantResponse =
        result.response ||
        "Your request was completed.";

      const assistantMessage:
        ChatMessage = {
        id:
          `${Date.now()}-assistant`,

        sender: "assistant",

        text: assistantResponse,
      };

      /*
       * First display the reply in the chat.
       */
      setMessages(
        (previous) => [
          ...previous,
          assistantMessage,
        ]
      );

      /*
       * Then speak that exact same reply.
       * This call is deliberately not awaited,
       * so speech cannot delay the UI.
       */
      void speakAssistantResponse(
        assistantResponse
      );
    } catch (error) {
      const errorMessage:
        ChatMessage = {
        id:
          `${Date.now()}-error`,

        sender: "assistant",

        text:
          error instanceof Error
            ? error.message
            : "Something went wrong. Please try again.",
      };

      setMessages(
        (previous) => [
          ...previous,
          errorMessage,
        ]
      );
    } finally {
      setLoading(false);
    }
  };

  useSpeechRecognitionEvent(
    "start",
    () => {
      setRecognizing(true);
    }
  );

  useSpeechRecognitionEvent(
    "end",
    () => {
      setRecognizing(false);
    }
  );

  useSpeechRecognitionEvent(
    "result",
    (event) => {
      const transcript =
        event.results[0]
          ?.transcript || "";

      if (!transcript.trim()) {
        return;
      }

      setMessage(transcript);

      if (!event.isFinal) {
        return;
      }

      if (
        voiceSubmittedRef.current
      ) {
        return;
      }

      voiceSubmittedRef.current =
        true;

      void submitMessage(
        transcript
      );
    }
  );

  useSpeechRecognitionEvent(
    "error",
    (event) => {
      console.log(
        "Speech recognition:",
        event.error,
        event.message
      );

      setRecognizing(false);

      voiceSubmittedRef.current =
        false;
    }
  );

  const startListening =
    async () => {
      if (loading) {
        return;
      }

      try {
        const permission =
          await ExpoSpeechRecognitionModule
            .requestPermissionsAsync();

        if (
          !permission.granted
        ) {
          return;
        }

        voiceSubmittedRef.current =
          false;

        setMessage("");

        ExpoSpeechRecognitionModule.start(
          {
            lang: "en-US",
            interimResults: true,
            continuous: false,
          }
        );
      } catch (error) {
        console.log(
          "Voice recognition:",
          error
        );

        voiceSubmittedRef.current =
          false;
      }
    };

  const stopListening = () => {
    ExpoSpeechRecognitionModule.stop();
  };

  const sendTypedMessage = () => {
    void submitMessage(message);
  };

  const logout = async () => {
    if (recognizing) {
      ExpoSpeechRecognitionModule.stop();
    }

    await signOut();
  };

  return (
    <SafeAreaView
      style={styles.container}
      edges={["top"]}
    >
    <KeyboardAvoidingView
      style={styles.container}
      behavior={
        Platform.OS === "ios"
          ? "padding"
          : "height"
      }
    >
        <View style={styles.header}>
          <View style={styles.logo}>
            <Text
              style={styles.logoText}
            >
              🧠
            </Text>
          </View>

          <View
            style={styles.headerText}
          >
            <Text style={styles.title}>
              Alzheimer's Assistant
            </Text>

            <Text
              style={styles.subtitle}
            >
              {session?.fullName
                ? `Hello, ${session.fullName}`
                : "How can I help you?"}
            </Text>
          </View>

          <TouchableOpacity
            style={styles.logoutButton}
            onPress={logout}
            accessibilityRole="button"
            accessibilityLabel="Log out"
          >
            <Text
              style={
                styles.logoutText
              }
            >
              Logout
            </Text>
          </TouchableOpacity>
        </View>

        <ScrollView
          ref={scrollViewRef}
          style={styles.chatArea}
          contentContainerStyle={
            styles.chatContent
          }
          keyboardShouldPersistTaps="handled"
          onContentSizeChange={() => {
            scrollViewRef.current?.scrollToEnd({
              animated: true,
            });
          }}
        >
          {messages.length === 0 && (
            <View
              style={
                styles.welcomeContainer
              }
            >
              <View
                style={
                  styles.welcomeIconBox
                }
              >
                <Text
                  style={
                    styles.welcomeIcon
                  }
                >
                  👋
                </Text>
              </View>

              <Text
                style={
                  styles.welcomeTitle
                }
              >
                How can I help?
              </Text>

              <Text
                style={
                  styles.welcomeText
                }
              >
                Type or speak naturally.
                I can help with memories,
                reminders, contacts and
                questions.
              </Text>

              <View
                style={
                  styles.voiceHelpBox
                }
              >
                <Text
                  style={
                    styles.voiceHelp
                  }
                >
                  🎤 Tap the microphone
                  and speak. Your request
                  is sent automatically
                  when you finish.
                </Text>
              </View>
            </View>
          )}

          {messages.map(
            (item) => (
              <View
                key={item.id}
                style={[
                  styles.messageRow,

                  item.sender ===
                  "user"
                    ? styles.userRow
                    : styles.assistantRow,
                ]}
              >
                {item.sender ===
                  "assistant" && (
                  <Text
                    style={
                      styles.assistantIcon
                    }
                  >
                    🧠
                  </Text>
                )}

                <View
                  style={[
                    styles.messageBubble,

                    item.sender ===
                    "user"
                      ? styles.userBubble
                      : styles.assistantBubble,
                  ]}
                >
                  <Text
                    style={[
                      styles.messageText,

                      item.sender ===
                        "user" &&
                        styles.userMessageText,
                    ]}
                  >
                    {item.text}
                  </Text>
                </View>
              </View>
            )
          )}

          {loading && (
            <View
              style={[
                styles.messageRow,
                styles.assistantRow,
              ]}
            >
              <Text
                style={
                  styles.assistantIcon
                }
              >
                🧠
              </Text>

              <View
                style={
                  styles.loadingBubble
                }
              >
                <ActivityIndicator
                  size="small"
                  color="#6C5CE7"
                />

                <Text
                  style={
                    styles.thinkingText
                  }
                >
                  Thinking...
                </Text>
              </View>
            </View>
          )}
        </ScrollView>

        {recognizing && (
          <View
            style={
              styles.listeningContainer
            }
          >
            <Text
              style={
                styles.listeningText
              }
            >
              🎤 Listening...
            </Text>
          </View>
        )}

        <View
          style={
            styles.inputContainer
          }
        >
          <TextInput
            style={styles.input}
            placeholder={
              recognizing
                ? "Listening..."
                : "Type a message..."
            }
            placeholderTextColor="#9CA3AF"
            value={message}
            onChangeText={setMessage}
            multiline
            editable={
              !loading &&
              !recognizing
            }
          />

          <TouchableOpacity
            style={[
              styles.voiceButton,

              recognizing &&
                styles.voiceButtonActive,
            ]}
            onPress={
              recognizing
                ? stopListening
                : startListening
            }
            disabled={loading}
            activeOpacity={0.8}
          >
            <Text
              style={styles.voiceIcon}
            >
              {recognizing
                ? "⏹️"
                : "🎤"}
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.sendButton,

              (
                !message.trim() ||
                loading ||
                recognizing
              ) &&
                styles.disabledButton,
            ]}
            onPress={
              sendTypedMessage
            }
            disabled={
              !message.trim() ||
              loading ||
              recognizing
            }
          >
            <Text
              style={styles.sendText}
            >
              ➤
            </Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles =
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: "#F7F7FC",
    },

    header: {
      flexDirection: "row",
      alignItems: "center",
      paddingHorizontal: 16,
      paddingVertical: 13,
      backgroundColor: "#FFFFFF",
      borderBottomWidth: 1,
      borderBottomColor: "#E8E7F0",
    },

    logo: {
      width: 44,
      height: 44,
      borderRadius: 13,
      justifyContent: "center",
      alignItems: "center",
      marginRight: 10,
      backgroundColor: "#EEEAFE",
    },

    logoText: {
      fontSize: 23,
    },

    headerText: {
      flex: 1,
    },

    title: {
      fontSize: 18,
      fontWeight: "700",
      color: "#1F2937",
    },

    subtitle: {
      marginTop: 2,
      fontSize: 13,
      color: "#6B7280",
    },

    logoutButton: {
      paddingHorizontal: 12,
      paddingVertical: 8,
      marginRight: 45,
      borderRadius: 9,
      backgroundColor: "#F2F0FF",
    },
    logoutText: {
      fontSize: 13,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    chatArea: {
      flex: 1,
    },

    chatContent: {
      flexGrow: 1,
      padding: 16,
      paddingBottom: 25,
    },

    welcomeContainer: {
      flex: 1,
      minHeight: 350,
      justifyContent: "center",
      alignItems: "center",
      paddingHorizontal: 28,
    },

    welcomeIconBox: {
      width: 70,
      height: 70,
      borderRadius: 22,
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: "#EEEAFE",
    },

    welcomeIcon: {
      fontSize: 34,
    },

    welcomeTitle: {
      marginTop: 16,
      fontSize: 25,
      fontWeight: "700",
      color: "#1F2937",
    },

    welcomeText: {
      marginTop: 8,
      maxWidth: 320,
      fontSize: 16,
      lineHeight: 24,
      textAlign: "center",
      color: "#6B7280",
    },

    voiceHelpBox: {
      marginTop: 17,
      paddingHorizontal: 16,
      paddingVertical: 12,
      borderRadius: 13,
      backgroundColor: "#EAFBF8",
    },

    voiceHelp: {
      maxWidth: 290,
      fontSize: 13,
      lineHeight: 20,
      textAlign: "center",
      color: "#0F766E",
    },

    messageRow: {
      flexDirection: "row",
      marginBottom: 14,
    },

    userRow: {
      justifyContent: "flex-end",
    },

    assistantRow: {
      justifyContent: "flex-start",
      alignItems: "flex-start",
    },

    assistantIcon: {
      marginTop: 7,
      marginRight: 7,
      fontSize: 20,
    },

    messageBubble: {
      maxWidth: "80%",
      paddingHorizontal: 15,
      paddingVertical: 11,
      borderRadius: 16,
    },

    userBubble: {
      backgroundColor: "#6C5CE7",
      borderBottomRightRadius: 4,
    },

    assistantBubble: {
      backgroundColor: "#FFFFFF",
      borderWidth: 1,
      borderColor: "#E5E7EB",
      borderBottomLeftRadius: 4,
    },

    loadingBubble: {
      flexDirection: "row",
      alignItems: "center",
      paddingHorizontal: 15,
      paddingVertical: 12,
      borderRadius: 16,
      backgroundColor: "#FFFFFF",
      borderWidth: 1,
      borderColor: "#E5E7EB",
    },

    messageText: {
      fontSize: 16,
      lineHeight: 23,
      color: "#1F2937",
    },

    userMessageText: {
      color: "#FFFFFF",
    },

    thinkingText: {
      marginLeft: 8,
      fontSize: 14,
      color: "#6B7280",
    },

    listeningContainer: {
      alignItems: "center",
      paddingVertical: 9,
      backgroundColor: "#EAFBF8",
    },

    listeningText: {
      fontSize: 14,
      fontWeight: "700",
      color: "#0F766E",
    },

    inputContainer: {
      flexDirection: "row",
      alignItems: "flex-end",
      paddingHorizontal: 12,
      paddingTop: 10,
      paddingBottom: 12,
      backgroundColor: "#FFFFFF",
      borderTopWidth: 1,
      borderTopColor: "#E8E7F0",
    },

    input: {
      flex: 1,
      minHeight: 48,
      maxHeight: 110,
      paddingHorizontal: 14,
      paddingVertical: 11,
      borderWidth: 1,
      borderColor: "#D1D5DB",
      borderRadius: 24,
      backgroundColor: "#FAFAFD",
      fontSize: 16,
      color: "#1F2937",
    },

    voiceButton: {
      width: 48,
      height: 48,
      marginLeft: 8,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 24,
      backgroundColor: "#EAFBF8",
    },

    voiceButtonActive: {
      backgroundColor: "#CCFBF1",
    },

    voiceIcon: {
      fontSize: 21,
    },

    sendButton: {
      width: 48,
      height: 48,
      marginLeft: 8,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 24,
      backgroundColor: "#6C5CE7",
    },

    disabledButton: {
      opacity: 0.4,
    },

    sendText: {
      fontSize: 21,
      fontWeight: "700",
      color: "#FFFFFF",
    },
  });