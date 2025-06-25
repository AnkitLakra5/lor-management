/**
 * Utility function to handle complete logout and redirect to home page
 * This ensures all cached data is cleared and user is properly redirected
 */
export const performLogout = (): void => {
  // Clear all localStorage data
  localStorage.clear();
  
  // Clear sessionStorage as well
  sessionStorage.clear();
  
  // Clear any cached API responses
  if (window.caches) {
    caches.keys().then(names => {
      names.forEach(name => {
        caches.delete(name);
      });
    });
  }
  
  // Clear any cookies (if any)
  document.cookie.split(";").forEach(function(c) { 
    document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
  });
  
  // Force a hard redirect to home page to ensure fresh load
  window.location.replace('/');
};
