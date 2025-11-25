
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Hello")
public class Hello extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Hello() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/jpeg");
		BufferedImage image = new BufferedImage(400,300,BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 400, 300);
		g.setColor(Color.BLUE);
		g.fillOval(100, 100, 100, 100);
		
		g.setColor(Color.RED);
		g.setFont(new Font("arial",Font.BOLD,40));
		g.drawString("Hello", 50, 50);
		OutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
