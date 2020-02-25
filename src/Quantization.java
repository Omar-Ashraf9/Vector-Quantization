import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class Quantization 
{
	public static String pathresult = "G:\\photos for MM assignment #4\\compressed.jpg";
	public static List<List<Integer>> blocks = new ArrayList<List<Integer>> ();
	public static List<List<Integer>> Quantizedvalues = new ArrayList<List<Integer>> ();
	public static List<Integer> indexInCodeBook = new ArrayList<Integer> ();
	public static int result2DArray[][];
	public static int pixel[][];
	public static int imagesize;
	public static int width;
	public static int height;
	public static int imageType;
	
	
	public static Scanner input = new Scanner(System.in);
//__________________________Methods_________________________________
	public static int GetNewSize(String path, int Blocksize)
	{
		int newsize = 0;
		try 
		{
		BufferedImage image;
		File input 	= new File( path );
		
		image 		= ImageIO.read(input);
			
		width 		= image.getWidth();
		height 		= image.getHeight();
		
		newsize = Math.min(width, height);
		
		while(newsize % Blocksize != 0)
		{
			newsize--;
		}
		
		}
		catch(Exception e)
		{
			System.out.println( "Image can't be readed" );
		}
		return newsize;
	}
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	public static void readImage_grayScale(String path, int size)
	{
		try 
		{
			BufferedImage image;
			File input 	= new File( path );
			
			image 		= ImageIO.read(input);
			
			
			Image newImage = image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
			image = toBufferedImage(newImage);
					
			width 		= image.getWidth();
			height 		= image.getHeight();
			imageType	= image.getType();
			
			
			pixel = new int[width][height];
			for (int i = 0; i < width; i++) 
			{
				for (int j = 0; j < height; j++) 
				{
					Color pixelColor = new Color( image.getRGB(i, j));
					int red 	= (int)(pixelColor.getRed()  );
					int green 	= (int)(pixelColor.getGreen());
					int blue 	= (int)(pixelColor.getBlue() );	
					pixel[i][j] = (red + green + blue) / 3;
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println( "Image can't be readed" );
		}
	}
	public static void saveImage_grayScale(String savePath , String imageName , String imageFormat)
	{
		
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int y = 0; y < width; y++) 
        {
            for (int x = 0; x < height; x++) 
            {
                int value = result2DArray[y][x];
                int fullrgb = (value) | (value << 16) | (value << 8) | value;
                img.setRGB(y, x, fullrgb);
            }
        }

        File f = new File(pathresult);

        try 
        {
            ImageIO.write(img, "jpeg", f);
        } catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public static void Getblocks(int[][] pixel, int Blocksize)
	{
		for(int i = 0; i < imagesize; i += Blocksize)
		{
			for(int j = 0; j < imagesize; j += Blocksize)
			{
				List <Integer> l = new ArrayList<Integer>();
				
				for(int x = i; x < i + Blocksize; x++)
				{
					for(int y = j; y < j + Blocksize; y++)
					{
						l.add(pixel[x][y]);
					}
				}
				blocks.add(l);
			}
		}
	}
	public static List<Integer> CalcAverage(List<List<Integer>> blocks, int Blocksize)
	{
		int[] ResultAtEachIndex = new int[Blocksize * Blocksize];
		for(List<Integer> l : blocks)
		{
			for(int i = 0; i < l.size(); i++)
			{
				ResultAtEachIndex[i] += l.get(i); 
			}
		}
		List<Integer> returned = new ArrayList<Integer>();
		for(int i = 0; i < ResultAtEachIndex.length; i++)
		{
			returned.add(ResultAtEachIndex[i] / blocks.size());
		}
		return returned;
	}
	public static int calcDistance(List<Integer> l1 , List<Integer> l2 , int added)
	{
		int dist = 0;
		for(int i = 0; i < l1.size(); i++)
		{
			dist += Math.pow(l1.get(i) - (l2.get(i) + added), 2);
		}
		return dist;
	}
	
	public static void GetQuantize(int numofcodebook , List<List<Integer>> blocks, List<List<Integer>> Quantizedvalues, int Blocksize)
	{
		if(numofcodebook == 1)
		{
			Quantizedvalues.add(CalcAverage(blocks,Blocksize));
			return;
		}
		List<List<Integer>> left = new ArrayList<List<Integer>>();
		List<List<Integer>> right = new ArrayList<List<Integer>> ();
		List<Integer> average = CalcAverage(blocks, Blocksize);
		
		for(List<Integer> l : blocks)
		{
			int dist1 = calcDistance(l, average,1);
			int dist2 = calcDistance(l, average, -1);
			
			
			if(dist1 > dist2)
			{
				left.add(l);
			}else
			{
				right.add(l);
			}
		}
		
		GetQuantize(numofcodebook / 2, left, Quantizedvalues, Blocksize);
		GetQuantize(numofcodebook / 2, right, Quantizedvalues, Blocksize);	
	}
	public static void MapToCorrectIndex(List<List<Integer>> blocks , List<List<Integer>> Quantizedvalues)
	{
		for(List<Integer> l : blocks)
		{
			int value = calcDistance(l, Quantizedvalues.get(0), 0);
			int index = 0;
			
			for(int i = 1; i < Quantizedvalues.size(); i++)
			{
				int tocheck = calcDistance(l, Quantizedvalues.get(i), 0);
				if(tocheck < value)
				{
					value = tocheck;
					index = i;
				}
			}
			indexInCodeBook.add(index);
		}
	}
	public static void compression(String path, int Blocksize, int numOfCodeBook) throws IOException
	{
		imagesize = GetNewSize(path , Blocksize);
		readImage_grayScale(path, imagesize);
		Getblocks(pixel, Blocksize);
		GetQuantize(numOfCodeBook , blocks , Quantizedvalues , Blocksize);
		MapToCorrectIndex(blocks , Quantizedvalues);
		
		 FileOutputStream fileOutputStream = new FileOutputStream("G:\\photos for MM assignment #4\\outputofcompression.txt");
	     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

	       	objectOutputStream.writeObject(width);
	        objectOutputStream.writeObject(height);
	        objectOutputStream.writeObject(Blocksize);
	        objectOutputStream.writeObject(indexInCodeBook);
	        objectOutputStream.writeObject(Quantizedvalues);
	        objectOutputStream.close();
		///======================================

	}
	public static void Decompression() throws IOException, ClassNotFoundException
	{
		InputStream file = new FileInputStream("G:\\photos for MM assignment #4\\outputofcompression.txt");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        int widthInDecompression = (int) input.readObject();
        int heightInDecompression = (int) input.readObject();
        int BlocksizeInDecompression = (int) input.readObject();
        List<Integer> indeciesInDecompression = (List<Integer>)input.readObject();
        List<List<Integer>> QuantizedvaluesInDecompression = (List<List<Integer>>) input.readObject();
        List<List<Integer>> imageValues = new ArrayList<List<Integer>>() ;
        
        // 0 1 3
        // 
        for(int i = 0 ; i < indeciesInDecompression.size() ; i++)
        {
        	imageValues.add(QuantizedvaluesInDecompression.get(indeciesInDecompression.get(i)));
        }
        
        //  			index
        // h h h h 	 h	
        //(0,1,3,5) (0,2,5,4) (0,3,3,1) (0,3,3,1) (0,3,3,1) (0,3,3,1) (0,3,3,1) (0,3,3,1) (0,3,3,1)
        //    j
        //	  y
        // ix[0] [1] [] [] [] []
        //  x[3] [5] [] [] [] []
        //   [] [] [] [] [] []
        //   [] [] [] [] [] []
        // 	 [] [] [] [] [] []
        //   [] [] [] [] [] []
        int indexInlist = 0;
        result2DArray = new int[imagesize][imagesize];
        int w = 0, h = 0;
        for(int i = 0 ; i < imagesize ; i+= BlocksizeInDecompression)
        {
        	for(int j = 0 ; j < imagesize ; j+= BlocksizeInDecompression)
        	{
        		for(int x = i; x < i + BlocksizeInDecompression ; x++)
        		{
        			for(int y = j; y < j + BlocksizeInDecompression ; y++)
        			{
        					result2DArray[x][y] = imageValues.get(indexInlist).get(h); 
        					h++;
        			}
        		}
        		h=0;
        		indexInlist++;
        	}
        }
       
        String save_imagePath 		= "G:\\photos for MM assignment #4\\";	
		String save_imageName 		= "compressed";
		String save_imageFormat		= "jpg";
		
		saveImage_grayScale(save_imagePath , save_imageName , save_imageFormat);
       
        
	}
	
}
