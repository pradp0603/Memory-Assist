import { Platform } from "react-native";
import * as SecureStore from "expo-secure-store";

export type AuthSession = {
  token: string;
  userId: number;
  email: string;
  fullName: string;
};

const AUTH_SESSION_KEY =
  "alzheimer_assistant_auth_session";

export async function saveAuthSession(
  session: AuthSession
): Promise<void> {
  const value = JSON.stringify(session);

  if (Platform.OS === "web") {
    if (typeof localStorage !== "undefined") {
      localStorage.setItem(
        AUTH_SESSION_KEY,
        value
      );
    }

    return;
  }

  await SecureStore.setItemAsync(
    AUTH_SESSION_KEY,
    value
  );
}

export async function getAuthSession():
  Promise<AuthSession | null> {
  try {
    let value: string | null = null;

    if (Platform.OS === "web") {
      if (typeof localStorage !== "undefined") {
        value =
          localStorage.getItem(
            AUTH_SESSION_KEY
          );
      }
    } else {
      value =
        await SecureStore.getItemAsync(
          AUTH_SESSION_KEY
        );
    }

    if (!value) {
      return null;
    }

    const session =
      JSON.parse(value) as AuthSession;

    if (
      !session.token ||
      !session.email ||
      typeof session.userId !== "number"
    ) {
      await clearAuthSession();

      return null;
    }

    return session;
  } catch {
    await clearAuthSession();

    return null;
  }
}

export async function clearAuthSession():
  Promise<void> {
  if (Platform.OS === "web") {
    if (typeof localStorage !== "undefined") {
      localStorage.removeItem(
        AUTH_SESSION_KEY
      );
    }

    return;
  }

  await SecureStore.deleteItemAsync(
    AUTH_SESSION_KEY
  );
}