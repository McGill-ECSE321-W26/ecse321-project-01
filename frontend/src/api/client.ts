// This is a fetch wrapper that auto-attaches the JWT token to every request
const BASE_URL = 'http://localhost:8080/api'

// small helper function to retrieve token from localstorage
function getToken(): string | null {
  return localStorage.getItem('token')
}

// Define custom error class to include a status code which will be used in frontend
export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

export async function api<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...((options.headers as Record<string, string>) ?? {}), // extra headers if any
  }

  // Set authorization bearer token in header if user is logged in
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  // use fetch API to send the request
  const response = await fetch(`${BASE_URL}${path}`, {
    headers,
    ...options,
  })

  if (!response.ok) {
    let message = response.statusText
    try {
      const body = await response.json()
      if (body.message) {
        message = body.message
      }
    }
    catch {
      // response wasn't JSON, fall back to statusText
    }
    throw new ApiError(message, response.status)
  }

  // Check 204 No Content, prevent syntax error from parsing by return undefined (no response body)
  if (response.status === 204) {
    return undefined as T
  }

  return response.json()
}
