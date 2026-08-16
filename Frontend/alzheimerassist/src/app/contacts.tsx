import React, {
  useRef,
  useState,
} from "react";

import {
  ActivityIndicator,
  Alert,
  FlatList,
  KeyboardAvoidingView,
  Platform,
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

type Contact = {
  id: string;
  name: string;
  relationship: string;
  phoneNumber: string;
};

export default function ContactsScreen() {
  const [contacts, setContacts] =
    useState<Contact[]>([]);

  const [name, setName] =
    useState("");

  const [
    relationship,
    setRelationship,
  ] = useState("");

  const [
    phoneNumber,
    setPhoneNumber,
  ] = useState("");

  const [
    editingId,
    setEditingId,
  ] = useState<string | null>(
    null
  );

  const [
    originalName,
    setOriginalName,
  ] = useState("");

  const [loading, setLoading] =
    useState(false);

  const [
    backendResponse,
    setBackendResponse,
  ] = useState("");

  /*
   * Used so the form can move upward
   * when the phone keyboard opens.
   */
  const listRef =
    useRef<FlatList<Contact>>(null);

  const clearForm = () => {
    setName("");
    setRelationship("");
    setPhoneNumber("");
    setEditingId(null);
    setOriginalName("");
  };

  const saveContact =
    async () => {
      const cleanName =
        name.trim();

      const cleanRelationship =
        relationship.trim();

      const cleanPhoneNumber =
        phoneNumber.trim();

      if (
        !cleanName ||
        !cleanPhoneNumber
      ) {
        Alert.alert(
          "Missing information",
          "Please enter the contact name and phone number."
        );

        return;
      }

      if (loading) {
        return;
      }

      setLoading(true);
      setBackendResponse("");

      try {
        let message = "";

        if (editingId) {
          message =
            `Update my contact ${originalName}. ` +
            `The name is ${cleanName}, ` +
            `the relationship is ${
              cleanRelationship ||
              "contact"
            }, ` +
            `and the phone number is ${cleanPhoneNumber}.`;
        } else {
          message =
            `Save this contact. ` +
            `${cleanName} is my ${
              cleanRelationship ||
              "contact"
            }. ` +
            `Their phone number is ${cleanPhoneNumber}.`;
        }

        /*
         * No userId.
         * JWT identifies current user.
         */
        const result =
          await sendChatMessage({
            message,
          });

        setBackendResponse(
          result.response ||
            "Contact request completed."
        );

        if (editingId) {
          setContacts(
            (previous) =>
              previous.map(
                (contact) =>
                  contact.id ===
                  editingId
                    ? {
                        ...contact,
                        name:
                          cleanName,
                        relationship:
                          cleanRelationship,
                        phoneNumber:
                          cleanPhoneNumber,
                      }
                    : contact
              )
          );
        } else {
          const newContact:
            Contact = {
            id:
              Date.now()
                .toString(),

            name:
              cleanName,

            relationship:
              cleanRelationship,

            phoneNumber:
              cleanPhoneNumber,
          };

          setContacts(
            (previous) => [
              newContact,
              ...previous,
            ]
          );
        }

        clearForm();

        /*
         * Return to the top of the screen
         * after saving successfully.
         */
        setTimeout(() => {
          listRef.current?.scrollToOffset({
            offset: 0,
            animated: true,
          });
        }, 100);
      } catch (error) {
        setBackendResponse(
          error instanceof Error
            ? error.message
            : "Sorry, I couldn't process the contact request."
        );
      } finally {
        setLoading(false);
      }
    };

  const editContact = (
    contact: Contact
  ) => {
    setName(contact.name);

    setRelationship(
      contact.relationship
    );

    setPhoneNumber(
      contact.phoneNumber
    );

    setEditingId(
      contact.id
    );

    setOriginalName(
      contact.name
    );

    setBackendResponse("");

    /*
     * Move back to the form when
     * Edit is pressed.
     */
    setTimeout(() => {
      listRef.current?.scrollToOffset({
        offset: 0,
        animated: true,
      });
    }, 100);
  };

  const performDelete =
    async (
      contact: Contact
    ) => {
      if (loading) {
        return;
      }

      setLoading(true);
      setBackendResponse("");

      try {
        const result =
          await sendChatMessage({
            message:
              `Delete my contact ${contact.name}.`,
          });

        setBackendResponse(
          result.response ||
            "Contact deleted."
        );

        setContacts(
          (previous) =>
            previous.filter(
              (
                savedContact
              ) =>
                savedContact.id !==
                contact.id
            )
        );

        if (
          editingId ===
          contact.id
        ) {
          clearForm();
        }
      } catch (error) {
        setBackendResponse(
          error instanceof Error
            ? error.message
            : "Sorry, I couldn't delete the contact."
        );
      } finally {
        setLoading(false);
      }
    };

  const deleteContact = (
    contact: Contact
  ) => {
    Alert.alert(
      "Delete contact",
      `Delete ${contact.name}?`,
      [
        {
          text: "Cancel",
          style: "cancel",
        },

        {
          text: "Delete",
          style: "destructive",

          onPress: () => {
            void performDelete(
              contact
            );
          },
        },
      ]
    );
  };

  /*
   * When the user reaches the phone
   * number field, move the form upward
   * so Save Contact stays reachable.
   */
  const handlePhoneFocus = () => {
    setTimeout(() => {
      listRef.current?.scrollToOffset({
        offset: 240,
        animated: true,
      });
    }, 250);
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
        <FlatList
          ref={listRef}
          data={contacts}
          keyExtractor={
            (item) => item.id
          }

          /*
           * Entire page is now scrollable,
           * including the contact form.
           */
          keyboardShouldPersistTaps="handled"
          keyboardDismissMode="on-drag"
          showsVerticalScrollIndicator={false}

          contentContainerStyle={
            styles.listContent
          }

          /*
           * Everything that used to sit
           * above FlatList is now inside
           * its scrollable header.
           */
          ListHeaderComponent={
            <>
              {/* Header */}

              <View style={styles.header}>
                <View
                  style={styles.iconBox}
                >
                  <Text
                    style={styles.icon}
                  >
                    👥
                  </Text>
                </View>

                <Text
                  style={styles.title}
                >
                  My Contacts
                </Text>

                <Text
                  style={styles.subtitle}
                >
                  Keep important people easy
                  to find.
                </Text>
              </View>

              {/* Contact Form */}

              <View
                style={styles.formCard}
              >
                <Text
                  style={
                    styles.formTitle
                  }
                >
                  {editingId
                    ? "Update Contact"
                    : "Add Contact"}
                </Text>

                {/* Name */}

                <Text
                  style={styles.label}
                >
                  Name
                </Text>

                <TextInput
                  style={styles.input}
                  placeholder="Example: Priya"
                  placeholderTextColor="#9CA3AF"
                  value={name}
                  onChangeText={setName}
                  editable={!loading}
                />

                {/* Relationship */}

                <Text
                  style={styles.label}
                >
                  Relationship
                </Text>

                <TextInput
                  style={styles.input}
                  placeholder="Example: Daughter"
                  placeholderTextColor="#9CA3AF"
                  value={relationship}
                  onChangeText={
                    setRelationship
                  }
                  editable={!loading}
                />

                {/* Phone */}

                <Text
                  style={styles.label}
                >
                  Phone number
                </Text>

                <TextInput
                  style={styles.input}
                  placeholder="Example: 9876543210"
                  placeholderTextColor="#9CA3AF"
                  value={phoneNumber}
                  onChangeText={
                    setPhoneNumber
                  }
                  keyboardType="phone-pad"
                  editable={!loading}
                  onFocus={
                    handlePhoneFocus
                  }
                />

                {/* Save / Update */}

                <TouchableOpacity
                  style={[
                    styles.saveButton,

                    loading &&
                      styles.disabledButton,
                  ]}
                  onPress={
                    saveContact
                  }
                  disabled={loading}
                  activeOpacity={0.8}
                  accessibilityRole="button"
                  accessibilityLabel={
                    editingId
                      ? "Update contact"
                      : "Save contact"
                  }
                >
                  {loading ? (
                    <ActivityIndicator
                      color="#FFFFFF"
                    />
                  ) : (
                    <Text
                      style={
                        styles.saveButtonText
                      }
                    >
                      {editingId
                        ? "Update Contact"
                        : "Save Contact"}
                    </Text>
                  )}
                </TouchableOpacity>

                {/* Cancel editing */}

                {editingId && (
                  <TouchableOpacity
                    style={
                      styles.cancelButton
                    }
                    onPress={clearForm}
                    disabled={loading}
                  >
                    <Text
                      style={
                        styles.cancelButtonText
                      }
                    >
                      Cancel
                    </Text>
                  </TouchableOpacity>
                )}
              </View>

              {/* Backend Response */}

              {backendResponse !==
                "" && (
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
                    {backendResponse}
                  </Text>
                </View>
              )}

              {/* Contacts Heading */}

              <View
                style={
                  styles.sectionHeader
                }
              >
                <Text
                  style={
                    styles.sectionTitle
                  }
                >
                  Contacts
                </Text>

                <View
                  style={
                    styles.countBadge
                  }
                >
                  <Text
                    style={
                      styles.countText
                    }
                  >
                    {contacts.length}
                  </Text>
                </View>
              </View>
            </>
          }

          /*
           * Empty contacts state.
           */
          ListEmptyComponent={
            <View
              style={
                styles.emptyContainer
              }
            >
              <Text
                style={
                  styles.emptyIcon
                }
              >
                👤
              </Text>

              <Text
                style={
                  styles.emptyTitle
                }
              >
                No contacts shown
              </Text>

              <Text
                style={
                  styles.emptyText
                }
              >
                Add an important contact
                above.
              </Text>
            </View>
          }

          /*
           * Saved contacts.
           */
          renderItem={({
            item,
          }) => (
            <View
              style={
                styles.contactCard
              }
            >
              <View
                style={styles.avatar}
              >
                <Text
                  style={
                    styles.avatarText
                  }
                >
                  {item.name
                    .charAt(0)
                    .toUpperCase()}
                </Text>
              </View>

              <View
                style={
                  styles.contactInformation
                }
              >
                <Text
                  style={
                    styles.contactName
                  }
                >
                  {item.name}
                </Text>

                {item.relationship !==
                  "" && (
                  <Text
                    style={
                      styles.relationship
                    }
                  >
                    {item.relationship}
                  </Text>
                )}

                <Text
                  style={
                    styles.phoneNumber
                  }
                >
                  {item.phoneNumber}
                </Text>
              </View>

              <View
                style={styles.actions}
              >
                <TouchableOpacity
                  style={
                    styles.editButton
                  }
                  onPress={() =>
                    editContact(
                      item
                    )
                  }
                  disabled={loading}
                >
                  <Text
                    style={
                      styles.editButtonText
                    }
                  >
                    Edit
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={
                    styles.deleteButton
                  }
                  onPress={() =>
                    deleteContact(
                      item
                    )
                  }
                  disabled={loading}
                >
                  <Text
                    style={
                      styles.deleteButtonText
                    }
                  >
                    Delete
                  </Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
        />
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

    /*
     * Entire FlatList content.
     */
    listContent: {
      paddingBottom: 35,
    },

    /*
     * Header
     */
    header: {
      alignItems: "center",
      paddingHorizontal: 20,
      paddingTop: 18,
      paddingBottom: 18,
    },

    iconBox: {
      width: 64,
      height: 64,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 20,
      backgroundColor: "#EEEAFE",
    },

    icon: {
      fontSize: 34,
    },

    title: {
      marginTop: 10,
      fontSize: 28,
      fontWeight: "700",
      color: "#1F2937",
    },

    subtitle: {
      marginTop: 6,
      fontSize: 15,
      color: "#6B7280",
      textAlign: "center",
    },

    /*
     * Contact form
     */
    formCard: {
      marginHorizontal: 16,
      padding: 18,
      backgroundColor: "#FFFFFF",
      borderRadius: 16,
      borderWidth: 1,
      borderColor: "#E3E1ED",
    },

    formTitle: {
      marginBottom: 16,
      fontSize: 19,
      fontWeight: "700",
      color: "#1F2937",
    },

    label: {
      marginBottom: 7,
      fontSize: 15,
      fontWeight: "600",
      color: "#374151",
    },

    input: {
      minHeight: 48,
      marginBottom: 14,
      paddingHorizontal: 14,
      borderWidth: 1,
      borderColor: "#D1D5DB",
      borderRadius: 10,
      fontSize: 16,
      color: "#1F2937",
      backgroundColor: "#FAFAFD",
    },

    saveButton: {
      minHeight: 50,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 10,
      backgroundColor: "#6C5CE7",
    },

    disabledButton: {
      opacity: 0.6,
    },

    saveButtonText: {
      fontSize: 16,
      fontWeight: "700",
      color: "#FFFFFF",
    },

    cancelButton: {
      alignItems: "center",
      paddingVertical: 12,
    },

    cancelButtonText: {
      fontSize: 15,
      color: "#6B7280",
    },

    /*
     * Assistant response
     */
    responseCard: {
      marginHorizontal: 16,
      marginTop: 14,
      padding: 16,
      backgroundColor: "#FFFFFF",
      borderRadius: 14,
      borderWidth: 1,
      borderColor: "#E3E1ED",
    },

    responseTitle: {
      fontSize: 16,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    responseText: {
      marginTop: 7,
      fontSize: 15,
      lineHeight: 22,
      color: "#4B5563",
    },

    /*
     * Contacts section header
     */
    sectionHeader: {
      flexDirection: "row",
      alignItems: "center",
      marginTop: 20,
      marginBottom: 10,
      paddingHorizontal: 18,
    },

    sectionTitle: {
      flex: 1,
      fontSize: 20,
      fontWeight: "700",
      color: "#1F2937",
    },

    countBadge: {
      minWidth: 32,
      height: 32,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 16,
      backgroundColor: "#EEEAFE",
    },

    countText: {
      fontWeight: "700",
      color: "#6C5CE7",
    },

    /*
     * Contact card
     */
    contactCard: {
      flexDirection: "row",
      alignItems: "center",
      marginHorizontal: 16,
      marginBottom: 12,
      padding: 15,
      borderWidth: 1,
      borderColor: "#E5E7EB",
      borderRadius: 14,
      backgroundColor: "#FFFFFF",
    },

    avatar: {
      width: 45,
      height: 45,
      justifyContent: "center",
      alignItems: "center",
      borderRadius: 23,
      backgroundColor: "#EEEAFE",
    },

    avatarText: {
      fontSize: 19,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    contactInformation: {
      flex: 1,
      marginLeft: 12,
    },

    contactName: {
      fontSize: 18,
      fontWeight: "700",
      color: "#1F2937",
    },

    relationship: {
      marginTop: 3,
      fontSize: 14,
      color: "#6B7280",
    },

    phoneNumber: {
      marginTop: 4,
      fontSize: 15,
      color: "#374151",
    },

    actions: {
      marginLeft: 8,
    },

    editButton: {
      paddingHorizontal: 11,
      paddingVertical: 7,
      marginBottom: 6,
      borderRadius: 7,
      backgroundColor: "#EEEAFE",
    },

    editButtonText: {
      fontSize: 13,
      fontWeight: "700",
      color: "#6C5CE7",
    },

    deleteButton: {
      paddingHorizontal: 11,
      paddingVertical: 7,
      borderRadius: 7,
      backgroundColor: "#FEE2E2",
    },

    deleteButtonText: {
      fontSize: 13,
      fontWeight: "700",
      color: "#B91C1C",
    },

    /*
     * Empty state
     */
    emptyContainer: {
      alignItems: "center",
      paddingTop: 30,
      paddingBottom: 30,
    },

    emptyIcon: {
      fontSize: 36,
    },

    emptyTitle: {
      marginTop: 8,
      fontSize: 18,
      fontWeight: "700",
      color: "#1F2937",
    },

    emptyText: {
      marginTop: 5,
      fontSize: 14,
      color: "#6B7280",
    },
  });