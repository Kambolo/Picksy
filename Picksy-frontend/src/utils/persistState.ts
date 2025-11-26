export function saveState(itemName: string, state: any) {
  localStorage.setItem(itemName, JSON.stringify(state));
}

export function loadState(itemName: string) {
  const data = localStorage.getItem(itemName);
  return data ? JSON.parse(data) : null;
}

export function clearState(itemName: string) {
  localStorage.removeItem(itemName);
}
