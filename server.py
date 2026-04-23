#!/usr/bin/env python3
import http.server
import socketserver
import os

# Set the port
PORT = 3000

# Change to the project directory
os.chdir(os.path.dirname(os.path.abspath(__file__)))

class MyHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # Add CORS headers to allow backend API calls
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

if __name__ == "__main__":
    with socketserver.TCPServer(("", PORT), MyHTTPRequestHandler) as httpd:
        print(f"🚀 Eventra Frontend Server running at http://localhost:{PORT}")
        print(f"📁 Serving files from: {os.getcwd()}")
        print(f"🔗 Backend should be at: http://localhost:8080/eventra")
        print(f"🧪 Test connection at: http://localhost:{PORT}/test-connection.html")
        print(f"🎯 Main app at: http://localhost:{PORT}/index-backend.html")
        print(f"⏹️  Press Ctrl+C to stop the server")
        httpd.serve_forever()
