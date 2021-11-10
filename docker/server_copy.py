from http.server import BaseHTTPRequestHandler, HTTPServer
import time

hostName = "localhost"
serverPort = 9999
import cgi


class MyServer(BaseHTTPRequestHandler):
    def do_POST(self):
#        print("In post")
#        self.send_response(301)
#        self.send_header('Content-Type', 'text/html')
#        self.end_headers()
##        content_len = int(self.headers['Content-Length'])
##        print(content_len)
##        post_body = self.rfile.read(content_len)
##        print(post_body)
#
#        ctype, pdict = cgi.parse_header(self.headers.get('content-type'))
#        pdict['boundary'] = pdict['boundary'].decode("utf-8")
#        print(ctype)
#        print(pdict)
#        if ctype == 'multipart/form-data':
#            fields = cgi.parse_multipart(self.rfile, pdict)
#            messagecontent = fields.get('file')
#            print(fields.get('field1'))
            
        form = cgi.FieldStorage(
            fp=self.rfile,
            headers=self.headers,
            environ={'REQUEST_METHOD':'POST',
                     'CONTENT_TYPE':self.headers['Content-Type'],
                     })
        filename = form['file'].filename
        data = form['file'].file.read()
        print(form['field1'])
        open("/Users/jan/app_files/upload/%s"%filename, "wb").write(data)
        self.respond()
        
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        self.wfile.write(bytes("<html>", "utf-8"))
#        self.wfile.write(bytes("<p>Request: %s</p>" % self.path, "utf-8"))
        self.wfile.write(bytes("<body>", "utf-8"))
        self.wfile.write(bytes("<p>This is an example web server.</p>", "utf-8"))
        self.wfile.write(bytes("</body></html>", "utf-8"))
        
    def respond(self, status=200):
        response="uploaded"
        self.send_response(status)
        self.send_header("Content-type", "text/html")
        self.send_header("Content-length", len(response))
        self.end_headers()
        self.wfile.write(response)

if __name__ == "__main__":        
    webServer = HTTPServer((hostName, serverPort), MyServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")
