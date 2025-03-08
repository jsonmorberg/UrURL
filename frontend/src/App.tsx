import { useState } from 'react';
import './App.css';

function App() {
  const [url, setUrl] = useState('');
  const [shortenedUrl, setShortenedUrl] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission behavior

    console.log('URL submitted:', url); // Log the URL for debugging

    // Check if URL is empty or invalid
    if (!url) {
      setError('Please enter a valid URL.');
      return; // Stop the function execution if no URL is provided
    }

    try {
      const response = await fetch(`http://localhost:8080/create?originalUrl=${encodeURIComponent(url)}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      // Check for response status
      if (!response.ok) {
        setError('Failed to shorten URL. Try again.');
        return;
      }

      const data = await response.json();
      console.log('Response data:', data); // Log the response from the server
      setShortenedUrl(data.shortCode); // Assuming the response contains the short code
      setError(''); // Clear any previous error message
    } catch (err) {
      console.error('Error:', err); // Log the error
      setError('Something went wrong. Please try again.');
    }
  };

  return (
    <div className="App">
      <h1 className="text-4xl font-bold text-center py-4">URL Shortener</h1>
      <div className="flex justify-center items-center">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-lg">Enter your URL:</label>
            <input
              type="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="p-2 border border-gray-300 rounded"
              required
            />
          </div>
          <button
            type="submit"
            className="p-2 bg-blue-500 text-white rounded"
          >
            Shorten URL
          </button>
        </form>
      </div>
      {error && <div className="text-red-500 text-center py-4">{error}</div>}
      {shortenedUrl && (
        <div className="text-center py-4">
          <p>Your shortened URL:</p>
          <a
            href={`http://localhost:8080/${shortenedUrl}`}
            className="text-blue-600"
            target="_blank"
            rel="noopener noreferrer"
          >
            {`http://localhost:8080/${shortenedUrl}`}
          </a>
        </div>
      )}
    </div>
  );
}

export default App;
