package application.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import model.data.CompteCourant;
import model.data.Operation;

public class PdfUtilities {
	public static void insererCell(PdfPTable table, String text) {
		PdfPCell cell = new PdfPCell(new Paragraph(text));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		
		table.addCell(cell);
	}
	
	public static void genererReleve(String nomFichier, CompteCourant compte, ArrayList<Operation> operations) throws FileNotFoundException, DocumentException {
		Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
		
		PdfWriter.getInstance(doc, new FileOutputStream(nomFichier));
		
		doc.open();
		
		int nbOp = operations.size();
		
		Paragraph titre = new Paragraph(new Phrase("Relevé mensuel du compte numéro 5", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f)));
		titre.setAlignment(Element.ALIGN_CENTER);
		titre.setSpacingAfter(20);
		doc.add(titre);

		if(nbOp == 0) {
			Paragraph message = new Paragraph("Aucune opération n'a été réalisée durant ce mois.");
			doc.add(message);
		} else {
			
			PdfPTable table = new PdfPTable(5);
			
			insererCell(table, "ID");
			insererCell(table, "Montant");
			insererCell(table, "Date");
			insererCell(table, "Date valeur");
			insererCell(table, "Type");
			
			for(int i = 0; i < nbOp; i++) {
				Operation op = operations.get(i);
				
				insererCell(table, String.valueOf(op.idOperation));
				insererCell(table, String.valueOf(op.montant) + "€");
				insererCell(table, String.valueOf(op.dateOp));
				insererCell(table, String.valueOf(op.dateValeur));
				insererCell(table, op.idTypeOp);
			}
			
			doc.add(table);
		}
		
		doc.close();
	}
}
