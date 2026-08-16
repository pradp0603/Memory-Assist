import React, {
  createContext,
  PropsWithChildren,
  useContext,
  useEffect,
  useState,
} from "react";

import {
  AuthSession,
  clearAuthSession,
  getAuthSession,
  saveAuthSession,
} from "../services/authStorage";

import {
  loginUser,
  setUnauthorizedHandler,
} from "../services/api";

type AuthContextValue = {
  session: AuthSession | null;
  isLoading: boolean;

  /*
   * Message shown on Login screen when
   * the user's JWT/session has expired.
   */
  authMessage: string;

  signIn: (
    email: string,
    password: string
  ) => Promise<void>;

  signOut: () => Promise<void>;
};

const AuthContext =
  createContext<AuthContextValue | null>(
    null
  );

export function AuthProvider({
  children,
}: PropsWithChildren) {
  const [session, setSession] =
    useState<AuthSession | null>(null);

  const [isLoading, setIsLoading] =
    useState(true);

  const [
    authMessage,
    setAuthMessage,
  ] = useState("");

  /*
   * Restore saved login session
   * when the app opens.
   */
  useEffect(() => {
    const restoreSession =
      async () => {
        try {
          const storedSession =
            await getAuthSession();

          setSession(storedSession);
        } catch (error) {
          console.log(
            "Could not restore session:",
            error
          );

          setSession(null);
        } finally {
          setIsLoading(false);
        }
      };

    void restoreSession();
  }, []);

  /*
   * api.ts calls this handler whenever
   * a protected request returns
   * 401 or 403.
   *
   * api.ts clears the stored JWT.
   * Here we clear the React session
   * and set the message for Login.
   */
  useEffect(() => {
    setUnauthorizedHandler(() => {
      setAuthMessage(
        "Your session has expired. Please sign in again."
      );

      setSession(null);
    });

    return () => {
      setUnauthorizedHandler(null);
    };
  }, []);

  /*
   * Login.
   */
  const signIn = async (
    email: string,
    password: string
  ) => {
    const result =
      await loginUser({
        email,
        password,
      });

    const nextSession:
      AuthSession = {
      token: result.token,
      userId: result.userId,
      email: result.email,
      fullName: result.fullName,
    };

    /*
     * Password is NEVER stored.
     * Only the authenticated session
     * information is saved.
     */
    await saveAuthSession(
      nextSession
    );

    /*
     * Successful login means the
     * session-expired message is no
     * longer needed.
     */
    setAuthMessage("");

    setSession(nextSession);
  };

  /*
   * Normal user logout.
   *
   * Do NOT show "session expired"
   * when the user deliberately logs out.
   */
  const signOut = async () => {
    await clearAuthSession();

    setAuthMessage("");

    setSession(null);
  };

  return (
    <AuthContext.Provider
      value={{
        session,
        isLoading,
        authMessage,
        signIn,
        signOut,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context =
    useContext(AuthContext);

  if (!context) {
    throw new Error(
      "useAuth must be used inside AuthProvider"
    );
  }

  return context;
}