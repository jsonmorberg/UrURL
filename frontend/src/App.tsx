import { useState } from 'react';
import './App.css';

function App() {
  const [url, setUrl] = useState('');
  const [shortenedUrl, setShortenedUrl] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // Prevent the default form submission behavior

    // Check if URL is empty or invalid
    if (!url) {
      setError('Please enter a valid URL.');
      return; 
    }

    // URL regex validation
    const urlPattern = /^(https?:\/\/)?([\w\d\-_]+\.)+[a-zA-Z]{2,7}(\/[^\s]*)?$/;
    if (!urlPattern.test(url)) {
      setError('Please enter a valid URL.');
      return;
    }

    setLoading(true); // Start loading

    try {
      // Make the request to the backend (via the proxy)
      const response = await fetch(`/create?originalUrl=${encodeURIComponent(url)}`, {
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
      setShortenedUrl(data.shortCode); 
      setError(''); // Clear any previous error message
      setUrl(''); // Clear URL input field
    } catch (err) {
      console.error('Error:', err); // Log the error
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false); // End loading
    }
  };

  return (
    <div className="App">
      <h1 className="text-4xl font-bold text-center py-4">URL Shortener</h1>
      <div className="flex justify-center items-center">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-lg mb-2">Enter your URL:</label>
            <input
              type="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="p-2 border border-gray-300 rounded mb-4"
              required
              aria-label="Enter your URL"
            />
          </div>
          <button
            type="submit"
            className="p-2 bg-blue-500 text-white rounded w-full"
            disabled={loading || !url}
          >
            {loading ? 'Shortening...' : 'Shorten URL'}
          </button>
        </form>
      </div>
      {error && <div className="text-red-500 text-center py-4">{error}</div>}
      {shortenedUrl && (
        <div className="text-center py-4">
          <p>Your shortened URL:</p>
          <a
            href={`http://ururl.xyz/${shortenedUrl}`}
            className="text-blue-600"
            target="_blank"
            rel="noopener noreferrer"
          >
            {`http://ururl.xyz/${shortenedUrl}`}
          </a>
        </div>
      )}
    </div>
  );
}

export default App;
