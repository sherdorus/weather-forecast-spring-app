document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('search-form');
  const cityInput = document.getElementById('city-input');
  const toastEl = document.getElementById('error-toast');
  const toastBody = document.getElementById('error-toast-body');
  const toast = new bootstrap.Toast(toastEl);

  form.addEventListener('submit', (e) => {
    const city = cityInput.value.trim();
    if (!city) {
      e.preventDefault();
      toastBody.textContent = 'Please enter a city name';
      toast.show();
    }
  });

  setTimeout(async () => {
    try {
      const city = cityInput.value.trim();
      const response = await fetch(`/weather?city=${encodeURIComponent(city)}`);
      if (response.ok) {
        location.reload();
      } else {
        toastBody.textContent = 'Failed to refresh weather data';
        toast.show();
      }
    } catch (error) {
      toastBody.textContent = 'Network error occurred';
      toast.show();
    }
  }, 600000);
});