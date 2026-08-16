import {
  clearAuthSession,
  getAuthSession,
} from "./authStorage";

export type RegisterRequest = {
  fullName: string;
  email: string;
  password: string;
  age: number;
};

export type RegisterResponse = {
  message?: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  token: string;
  userId: number;
  email: string;
  fullName: string;
};

export type ChatRequest = {
  message: string;
};

export type ChatResponse = {
  response: string;
};

const API_BASE_URL =
  "http://192.168.10.178:8080";

const REQUEST_TIMEOUT = 15000;

export class ApiError extends Error {
  status: number;

  constructor(
    status: number,
    message: string
  ) {
    super(message);

    this.name = "ApiError";
    this.status = status;
  }
}

type UnauthorizedHandler =
  () => void | Promise<void>;

let unauthorizedHandler:
  UnauthorizedHandler | null = null;

export function setUnauthorizedHandler(
  handler: UnauthorizedHandler | null
) {
  unauthorizedHandler = handler;
}

async function handleUnauthorized() {
  await clearAuthSession();

  if (unauthorizedHandler) {
    await unauthorizedHandler();
  }
}

function getResponseMessage(
  data: unknown,
  responseText: string,
  status: number
): string {
  if (
    data &&
    typeof data === "object"
  ) {
    const object =
      data as Record<string, unknown>;

    if (
      typeof object.message === "string" &&
      object.message.trim()
    ) {
      return object.message;
    }

    if (
      typeof object.response === "string" &&
      object.response.trim()
    ) {
      return object.response;
    }

    if (
      typeof object.error === "string" &&
      object.error.trim()
    ) {
      return object.error;
    }
  }

  if (responseText.trim()) {
    return responseText;
  }

  return `Server returned status ${status}`;
}

async function apiRequest<T>(
  path: string,
  method: "GET" | "POST",
  body?: unknown,
  includeAuthentication = true
): Promise<T> {
  const controller =
    new AbortController();

  const timeoutId = setTimeout(() => {
    controller.abort();
  }, REQUEST_TIMEOUT);

  try {
    const headers:
      Record<string, string> = {
        Accept: "application/json",
      };

    if (body !== undefined) {
      headers["Content-Type"] =
        "application/json";
    }

    if (includeAuthentication) {
      const session =
        await getAuthSession();

      if (!session?.token) {
        await handleUnauthorized();

        throw new ApiError(
          401,
          "Please sign in to continue."
        );
      }

      headers.Authorization =
        `Bearer ${session.token}`;
    }

    console.log(
      `${method} ${API_BASE_URL}${path}`
    );

    const response = await fetch(
      `${API_BASE_URL}${path}`,
      {
        method,
        headers,
        body:
          body === undefined
            ? undefined
            : JSON.stringify(body),
        signal: controller.signal,
      }
    );

    const responseText =
      await response.text();

    let data: unknown = null;

    if (responseText) {
      try {
        data = JSON.parse(responseText);
      } catch {
        data = responseText;
      }
    }

    /*
     * Expired / invalid JWT.
     *
     * Only protected requests trigger
     * automatic logout.
     */
      if (
        (response.status === 401 ||
          response.status === 403) &&
        includeAuthentication
      ) {
        /*
        * The JWT is missing, expired,
        * invalid, or no longer accepted.
        *
        * Remove the old token and force
        * the user back to Login.
        */
        await handleUnauthorized();

        throw new ApiError(
          401,
          "Your session has expired. Please sign in again."
        );
      }

    if (!response.ok) {
      throw new ApiError(
        response.status,
        getResponseMessage(
          data,
          responseText,
          response.status
        )
      );
    }

    if (
      response.status === 204 ||
      !responseText
    ) {
      return undefined as T;
    }

    return data as T;
  } catch (error: unknown) {
    if (error instanceof ApiError) {
      throw error;
    }

    if (
      error instanceof Error &&
      error.name === "AbortError"
    ) {
      throw new ApiError(
        0,
        "The request took too long. Please try again."
      );
    }

    if (
      error instanceof Error &&
      (
        error.message.includes(
          "fetch failed"
        ) ||
        error.message.includes(
          "Network request failed"
        ) ||
        error.message.includes(
          "Host unreachable"
        ) ||
        error.message.includes(
          "Failed to connect"
        ) ||
        error.message.includes(
          "NoRouteToHost"
        )
      )
    ) {
      throw new ApiError(
        0,
        "Cannot connect to the server. Check that Spring Boot is running and your phone and computer are on the same Wi-Fi."
      );
    }

    if (error instanceof Error) {
      throw new ApiError(
        0,
        error.message
      );
    }

    throw new ApiError(
      0,
      "Something went wrong. Please try again."
    );
  } finally {
    clearTimeout(timeoutId);
  }
}

export async function apiGet<T>(
  path: string
): Promise<T> {
  return apiRequest<T>(
    path,
    "GET"
  );
}

export async function apiPost<T>(
  path: string,
  body: unknown,
  includeAuthentication = true
): Promise<T> {
  return apiRequest<T>(
    path,
    "POST",
    body,
    includeAuthentication
  );
}

export async function registerUser(
  request: RegisterRequest
): Promise<RegisterResponse> {
  const result =
    await apiPost<RegisterResponse>(
      "/api/users/register",
      request,
      false
    );

  return result ?? {};
}

export async function loginUser(
  request: LoginRequest
): Promise<LoginResponse> {
  const result =
    await apiPost<LoginResponse>(
      "/api/auth/login",
      request,
      false
    );

  if (
    !result.token ||
    !result.email ||
    typeof result.userId !== "number"
  ) {
    throw new ApiError(
      0,
      "The login response from the server was invalid."
    );
  }

  return result;
}

export async function sendChatMessage(
  request: ChatRequest
): Promise<ChatResponse> {
  try {
    return await apiPost<ChatResponse>(
      "/api/chat",
      request
    );
  } catch (error) {
    /*
     * Preserve your existing behaviour:
     *
     * 404 Contact not found
     * becomes an assistant response,
     * not a red Expo error.
     */
    if (
      error instanceof ApiError &&
      error.status >= 400 &&
      error.status < 500 &&
      error.status !== 401 &&
      error.status !== 403
    ) {
      return {
        response: error.message,
      };
    }

    throw error;
  }
}