package com.sb.filenet.genericapi.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 
 * The class still needs to modified, once we get a confirmation to implement
 * the logic
 *
 */
@Component
public class DocumentConverter {

	private Logger logger = LogManager.getLogger(DocumentConverter.class);

	public String getBase64FromInputStream(InputStream inputStream) throws IOException {
		logger.debug("Start of getByteArrayFromInputStream method ");
		byte[] bytes;
		byte[] buffer = new byte[1024];
		try (BufferedInputStream is = new BufferedInputStream(inputStream)) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int length;
			while ((length = is.read(buffer)) > -1) {
				bos.write(buffer, 0, length);
			}
			bos.flush();
			bytes = bos.toByteArray();
		}
		String bas = Base64.getEncoder().encodeToString(bytes);
		logger.debug("End of getByteArrayFromInputStream method ");
		return bas;
	}

	/*
	 * // Testing purpose to ensure byte[] returns a valid file
	 * 
	 * private void getFileFromByteArray(byte[] array, String filename) throws
	 * FileNotFoundException, IOException { File outputFile = new
	 * File("C:\\Users\\Admin\\Documents\\Projects\\Logs\\SLMFilenetWebservices\\" + filename + "
	 * .pdf"); try (FileOutputStream outputStream = new
	 * FileOutputStream(outputFile)) { outputStream.write(array); } }
	 */

	/*
	 * public byte[] convertTiffToPdf(InputStream tiffFile) {
	 * 
	 * logger.debug("Start of convertTiffToPdf method ");
	 * 
	 * Document document = new Document(PageSize.LETTER, 0, 0, 0, 0);
	 * ByteArrayOutputStream pdfByte = new ByteArrayOutputStream(); try { PdfWriter
	 * writer = PdfWriter.getInstance(document, pdfByte); document.open();
	 * PdfContentByte pdfContentByte = writer.getDirectContent();
	 * RandomAccessFileOrArray randomAccessArray = null; int pages = 0; int
	 * numOfPages = 0; try { randomAccessArray = new
	 * RandomAccessFileOrArray(tiffFile); numOfPages =
	 * TiffImage.getNumberOfPages(randomAccessArray); } catch (Throwable e) { String
	 * message = "Exception occurred while converting tiff document to PDF: " +
	 * e.getLocalizedMessage(); logger.error(message); throw new
	 * ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message); }
	 * logger.debug("Processing tiff file::" + tiffFile); for (int c = 0; c <
	 * numOfPages; ++c) { try { Image image =
	 * TiffImage.getTiffImage(randomAccessArray, c + 1); if (image != null) {
	 * image.scalePercent(7200f / image.getDpiX(), 7200f / image.getDpiY());
	 * document.setPageSize(new Rectangle(image.getScaledWidth(),
	 * image.getScaledHeight())); image.setAbsolutePosition(0, 0);
	 * pdfContentByte.addImage(image); document.newPage(); ++pages; } } catch
	 * (Throwable e) { String message =
	 * "Exception occurred while converting tiff document to PDF: " +
	 * e.getLocalizedMessage(); logger.error(message); throw new
	 * ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message); } }
	 * randomAccessArray.close(); document.close(); } catch (Throwable e) { String
	 * message = "Exception occurred while converting tiff document to PDF: " +
	 * e.getLocalizedMessage(); logger.error(message); throw new
	 * ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message); }
	 * logger.debug("End of convertTiffToPdf method");
	 * 
	 * return pdfByte.toByteArray(); }
	 */
}
