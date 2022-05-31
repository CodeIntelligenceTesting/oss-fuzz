import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueHigh;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletRequestWrapper;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.CachedResource;
import org.apache.catalina.connector.*;
import org.apache.catalina.startup.Tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HttpParserFuzzer {

    private static class TestServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        /**
         * Only interested in the request headers from a GET request
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            // Just echo the header value back as plain text
            resp.setContentType("text/plain");

            PrintWriter out = resp.getWriter();

            // Enumeration<String> values = req.getHeaders("X-Bug48839");
            // while (values.hasMoreElements()) {
            //     out.println(values.nextElement());
            // }
        }
    }


    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        Tomcat tomcat = new Tomcat();

        Context root = tomcat.addContext("", System.getProperty("java.io.tmpdir"));
        Tomcat.addServlet(root, "Test", new TestServlet());
        // root.addServletMappingDecoded("/test", "Test");

        try {
            tomcat.start();
            // setProperty("port", String.valueOf(tomcat.getConnector().getLocalPort()));

            // Open connection
            final String encoding = "ISO-8859-1";
            SocketAddress addr = new InetSocketAddress("localhost", 8088);
            Socket socket = new Socket();
            socket.setSoTimeout(0);
            socket.connect(addr,0);
            OutputStream os = socket.getOutputStream();
            Writer writer = new OutputStreamWriter(os, encoding);
            InputStream is = socket.getInputStream();
            Reader r = new InputStreamReader(is, encoding);
            BufferedReader reader = new BufferedReader(r);

            String[] request = new String[1];
            request[0] = data.consumeRemainingAsString();
                // "GET http://localhost:8080/test HTTP/1.1" + CRLF +
                // "Host: localhost:8080" + CRLF +
                // "X-Bug48839: abcd" + CRLF +
                // "\tefgh" + CRLF +
                // "Connection: close" + CRLF +
                // CRLF;

            // setRequest(request);
            processRequest(); // blocks until response has been read

            // Close the connection
            disconnect();
        } catch (Exception e) {
        }
    }
}
