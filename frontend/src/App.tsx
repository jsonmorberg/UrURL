import { useState } from 'react';
import { ClipboardIcon, CheckIcon } from '@heroicons/react/24/solid';

const isValidUrl = (url: string): boolean => {
  const pattern = /^(https?:\/\/)?([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(:\d{1,5})?(\/.*)?$/;
  return pattern.test(url.trim());
};

const normalizeUrl = (url: string): string => {
  return url.startsWith('http://') || url.startsWith('https://')
    ? url.trim()  // Already valid, just clean any extra spaces
    : `http://${url.trim()}`;
};

function App() {
  const [url, setUrl] = useState<string>('');
  const [shortenedUrl, setShortenedUrl] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [copied, setCopied] = useState<boolean>(false);

  const showError = (message: string): void => {
    setError(message);
    setLoading(false);
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(`https://ururl.xyz/${shortenedUrl}`);
    setCopied(true);

    setTimeout(() => setCopied(false), 2000); // Reset "Copied!" message after 2 seconds
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();

    const fixedUrl = normalizeUrl(url); // Auto-fix missing scheme

    if (!isValidUrl(fixedUrl)) {
      showError('Please enter a valid URL.');
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`/create?originalUrl=${encodeURIComponent(fixedUrl)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      });

      if (!response.ok) {
        const errorData = await response.text();
        try {
          const parsedError = JSON.parse(errorData);
          showError(parsedError.message || 'Failed to shorten URL. Try again.');
        } catch {
          showError(errorData || 'Unknown error occurred. Please try again.');
        }
        return;
      }

      const data: { shortCode?: string } = await response.json();
      if (data.shortCode) {
        setShortenedUrl(data.shortCode);
        setError('');
        setUrl('');
      } else {
        showError('Unexpected response from server.');
      }
    } catch (err) {
      console.error('Error:', err);
      showError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-gray-900 to-gray-700 p-6">
      <div className="bg-white/20 backdrop-blur-lg rounded-2xl shadow-2xl p-8 w-full max-w-lg text-white border border-white/30">
        
        {/* Title */}
        <h1 className="text-4xl font-bold text-center mb-6 text-gray-100 drop-shadow-lg">
          URL <span className="text-white">Shortener</span>
        </h1>
  
        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <input
              id="urlInput"
              type="text"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              placeholder="https://example.com"
              required
              aria-label="Enter your URL"
              aria-describedby="url-description"
              inputMode='url'
              autoCapitalize='off'
              autoCorrect='off'
              spellCheck='false'
              className="w-full p-3 bg-white/30 border border-white/40 rounded-md text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-teal-400"
            />
          </div>
  
          <button
            type="submit"
            disabled={loading || !url}
            className={`w-full py-3 rounded-md font-bold text-white shadow-md border border-white/20 transition-all ${
              loading || !url
                ? 'bg-teal-500 opacity-50 cursor-not-allowed'
                : 'bg-gradient-to-r from-teal-500 to-teal-400 hover:scale-105 hover:shadow-xl'
            }`}
          >
            {loading ? 'Shortening...' : 'Shorten URL'}
          </button>
        </form>
  
        {/* Error Message */}
        {error && (
          <div className="mt-4 bg-red-600 text-white text-sm p-3 rounded-md text-center shadow-lg">
            {error}
          </div>
        )}
  
        {/* Success Message */}
        {shortenedUrl && (
          <div className="mt-4 text-center flex items-center justify-center gap-2">
            <p className="text-white">Shortened URL:</p>
            <a
              href={`https://ururl.xyz/${shortenedUrl}`}
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-300 underline hover:text-blue-400"
            >
              {`https://ururl.xyz/${shortenedUrl}`}
            </a>

            <button
              onClick={handleCopy}
              className="relative inline-flex items-center justify-center p-1"
              title="Copy to clipboard"
            >
              {/* Clipboard Icon */}
              <ClipboardIcon
                className={`h-5 w-5 absolute transition-opacity duration-200 ${copied ? 'opacity-0' : 'opacity-100'}`}
              />

              {/* Checkmark Icon */}
              <CheckIcon
                className={`h-5 w-5 absolute transition-opacity duration-200 ${copied ? 'opacity-100 text-green-400' : 'opacity-0'}`}
              />
            </button>
          </div>
        )}
      </div>
  
      {/* Footer */}
      <footer className="mt-8 text-white/50 text-sm text-center w-full">
        © {new Date().getFullYear()} Jayson Morberg — 
        <a 
          href="https://github.com/jsonmorberg/UrUrl"
          target="_blank"
          rel="noopener noreferrer"
          className="underline hover:text-white transition-all ml-1"
        >
          GitHub
        </a>
      </footer>
    </div>
  );
}

export default App;


