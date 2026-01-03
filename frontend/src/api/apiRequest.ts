const API_URL = "http://localhost:8080";
type HttpMethod = "GET" | "POST" | "PATCH" | "DELETE" | "PUT";

export async function apiRequest(
  endpoint: string,
  method: HttpMethod = "GET",
  secure: boolean = false,
  isForm: boolean = false,
  body?: any
) {
  try {
    const options: RequestInit = {
      method,
      credentials: secure ? "include" : "omit",
    };

    if (body && !isForm) {
      if (typeof body === "string") {
        options.headers = { "Content-Type": "text/plain" };
        options.body = body;
      } else {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
      }
    }

    if (body && isForm) {
      options.body = body; // FormData
    }

    const response = await fetch(`${API_URL}/${endpoint}`, options);

    const result = await response.json().catch(() => ({})); // Obsługa braku JSON-a

    if (!response.ok) {
      return {
        status: response.status,
        error: result.message || "Wystapił błąd",
      };
    }

    return {
      status: response.status,
      result: result,
    };
  } catch (error: any) {
    return {
      status: 500,
      error: error?.message || "Nieznany błąd",
    };
  }
}
