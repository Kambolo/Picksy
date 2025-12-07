import { apiRequest } from "./apiRequest";

export function createRoom(
  name: string,
  categorySets: { setId: number; categoryId: number }[]
) {
  return apiRequest("api/room/secure/create", "POST", true, false, {
    name,
    categories: categorySets,
  });
}

export function closeRoom(roomCode: string) {
  return apiRequest("api/room/secure/close", "POST", true, false, {
    roomCode,
  });
}

export function getRoomDetails(roomCode: string) {
  return apiRequest(`api/room/public/${roomCode}/details`, "GET", true);
}

export function startVoting(roomCode: string) {
  return apiRequest("api/room/secure/start", "POST", true, false, {
    roomCode,
  });
}

export function nextCategory(roomCode: string) {
  return apiRequest("api/room/secure/next", "POST", true, false, {
    roomCode,
  });
}

export function endVoting(roomCode: string) {
  return apiRequest("api/room/secure/finish", "POST", true, false, {
    roomCode,
  });
}

export function getResults(roomCode: string) {
  return apiRequest(`api/room/public/${roomCode}/results`);
}

export function getParticipantsCount(roomCode: string) {
  return apiRequest(`api/room/public/${roomCode}/participants`);
}

export function getRoomsHistory() {
  return apiRequest(`api/room/secure/history`, "GET", true);
}
