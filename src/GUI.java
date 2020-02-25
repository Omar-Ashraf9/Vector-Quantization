import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUI {

	private JFrame frame;
	private JTextField sizeofblock;
	private JTextField numOfBlocksInCodebook;
	public JButton Compression;
	public JButton Decompression;
	public String path;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton choose = new JButton("Choose Image");
		choose.setFont(new Font("Gadugi", Font.PLAIN, 12));
		choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 

                // invoke the showsOpenDialog function to show the save dialog 
                int r = j.showOpenDialog(null); 

                // if the user selects a file 
                if (r == JFileChooser.APPROVE_OPTION) 

                { 
                    // set the label to the path of the selected file 
                    path = j.getSelectedFile().getAbsolutePath();
                    Compression.setEnabled(true);
                    System.out.println(path);
                } 
                // if the user cancelled the operation 
                else
                    path = null;
			}
			
		});
		choose.setBounds(10, 10, 119, 35);
		frame.getContentPane().add(choose);
		
		Compression = new JButton("Compression");
		Compression.setFont(new Font("Gadugi", Font.PLAIN, 12));
		Compression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					Quantization.compression(path, Integer.parseInt(sizeofblock.getText()), Integer.parseInt(numOfBlocksInCodebook.getText()));
					Decompression.setEnabled(true);
				} catch (NumberFormatException | IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Compression.setEnabled(false);
		Compression.setBounds(10, 81, 119, 35);
		frame.getContentPane().add(Compression);
		
		Decompression = new JButton("Decompression");
		Decompression.setFont(new Font("Gadugi", Font.PLAIN, 12));
		Decompression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Quantization.Decompression();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Decompression.setEnabled(false);
		Decompression.setBounds(10, 166, 119, 35);
		frame.getContentPane().add(Decompression);
		
		JLabel lblSizeOfBlock = new JLabel("Size of block");
		lblSizeOfBlock.setHorizontalAlignment(SwingConstants.CENTER);
		lblSizeOfBlock.setFont(new Font("Gadugi", Font.PLAIN, 12));
		lblSizeOfBlock.setBounds(139, 10, 147, 35);
		frame.getContentPane().add(lblSizeOfBlock);
		
		sizeofblock = new JTextField();
		sizeofblock.setBounds(296, 10, 114, 35);
		frame.getContentPane().add(sizeofblock);
		sizeofblock.setColumns(10);
		
		JLabel lblNumOfBlocks = new JLabel("Number of blocks in codebook");
		lblNumOfBlocks.setFont(new Font("Gadugi", Font.PLAIN, 10));
		lblNumOfBlocks.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumOfBlocks.setBounds(139, 81, 147, 35);
		frame.getContentPane().add(lblNumOfBlocks);
		
		numOfBlocksInCodebook = new JTextField();
		numOfBlocksInCodebook.setBounds(296, 81, 114, 35);
		frame.getContentPane().add(numOfBlocksInCodebook);
		numOfBlocksInCodebook.setColumns(10);
	}
}
