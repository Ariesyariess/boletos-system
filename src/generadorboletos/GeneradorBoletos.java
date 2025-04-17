/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package generadorboletos;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import javax.swing.*;
import java.awt.GridLayout;

public class GeneradorBoletos {   
    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
          try {
            // Panel con campos editables
            JTextField campoTitulo = new JTextField("Bono colaboración.");
            JTextField campoPremio = new JTextField("una aspiradora robot.");
            JTextField campoFecha = new JTextField("Fecha: DD/MM/YYYY"); // Campo de fecha
            JTextField campoDescripcion = new JTextField("Escribe lo que quieras..."); // Campo para descripción
            JTextField campoInstitucion = new JTextField("Liceo de Castillos");
            JTextField campoValor = new JTextField("100");

            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Título (Ej: Bono colaboración):"));
            panel.add(campoTitulo);
            panel.add(new JLabel("Premio:"));
            panel.add(campoPremio);
            panel.add(new JLabel("Sorteo (DD/MM/YYYY):"));
            panel.add(campoFecha); // Campo para la fecha
            panel.add(new JLabel("Descripción (Ej: Quinielas, 5 de Oros, etc.):"));
            panel.add(campoDescripcion); // Campo para descripción
            panel.add(new JLabel("Institución beneficiada:"));
            panel.add(campoInstitucion);
            panel.add(new JLabel("Valor del boleto ($):"));
            panel.add(campoValor);

            int resultado = JOptionPane.showConfirmDialog(null, panel,
                    "Completar datos del boleto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (resultado != JOptionPane.OK_OPTION) return;

            String titulo = campoTitulo.getText().trim();
            String premio = campoPremio.getText().trim();
            String fecha = campoFecha.getText().trim(); // Tomamos el dato de la fecha
            String descripcion = campoDescripcion.getText().trim(); // Tomamos el dato de la descripción
            String institucion = campoInstitucion.getText().trim();
            String valorBoleto = campoValor.getText().trim();

            if (titulo.isEmpty() || premio.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El título y el premio no pueden estar vacíos.");
                return;
            }

            // Número inicial y final
            int numeroInicio = Integer.parseInt(JOptionPane.showInputDialog("Ingresa el número inicial de la rifa:"));
            int numeroFin = Integer.parseInt(JOptionPane.showInputDialog("Ingresa el número final de la rifa:"));

            if (numeroInicio > numeroFin) {
                JOptionPane.showMessageDialog(null, "El número inicial no puede ser mayor que el número final.");
                return;
            }

            int digitos = String.valueOf(numeroFin).length();

            int boletosPorPagina = Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos boletos deseas imprimir por hoja A4?"));

            // Seleccionar carpeta
            String rutaCarpeta = seleccionarCarpeta();
            if (rutaCarpeta == null) {
                JOptionPane.showMessageDialog(null, "No se seleccionó ninguna carpeta.");
                return;
            }

            String rutaArchivo = rutaCarpeta + "/boletos_rifa_castillos_horizontal.pdf";
            generarPDF(rutaArchivo, numeroInicio, numeroFin, digitos, titulo, premio, fecha, descripcion, institucion, valorBoleto, boletosPorPagina);

            JOptionPane.showMessageDialog(null, "PDF creado exitosamente en: " + rutaArchivo);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error: " + e.getMessage());
        }
    }

    private static String seleccionarCarpeta() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selector.setDialogTitle("Selecciona una carpeta para guardar el PDF");

        int resultado = selector.showSaveDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    private static void generarPDF(String ruta, int numeroInicio, int numeroFin, int digitos,
                                   String titulo, String premio, String fecha, String descripcion, String institucion,
                                   String valorBoleto, int boletosPorPagina) throws Exception {

        Document doc = new Document(new Rectangle(800, 300)); // horizontal
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();
        Font fuente = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

        int contador = 0;
        for (int i = numeroInicio; i <= numeroFin; i++) {
            doc.add(crearBoleto(i, fuente, digitos, titulo, premio, fecha, descripcion, institucion, valorBoleto));

            contador++;
            if (contador >= boletosPorPagina && i < numeroFin) {
                doc.newPage();
                contador = 0;
            }
        }

        doc.close();
    }

    private static PdfPTable crearBoleto(int num, Font fuente, int digitos,
                                         String titulo, String premio, String fecha, String descripcion,
                                         String institucion, String valorBoleto) throws DocumentException {
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new int[]{3, 1, 5});
        tabla.addCell(parteIzquierda(num, fuente, digitos, valorBoleto));
        tabla.addCell(lineaDivisoria());
        tabla.addCell(parteDerecha(num, fuente, digitos, titulo, premio, fecha, descripcion, institucion, valorBoleto));
        return tabla;
    }

    private static PdfPCell parteIzquierda(int num, Font fuente, int digitos, String valorBoleto) {
        String numeroFormateado = String.format("%0" + digitos + "d", num);
        String[] datos = {
                "Nombre: ____________________",
                "Dirección: __________________",
                "Teléfono: ___________________",
                "Valor: $" + valorBoleto,
                " ",
                "N°: " + numeroFormateado
        };
        return crearCeldaDesdeTexto(datos, fuente);
    }

    private static PdfPCell parteDerecha(int num, Font fuente, int digitos, String titulo,
                                         String premio, String fecha, String descripcion, String institucion, String valorBoleto) {
        String numeroFormateado = String.format("%0" + digitos + "d", num);

        PdfPTable subTabla = new PdfPTable(1);
        subTabla.setSpacingBefore(5);
        subTabla.setSpacingAfter(5);

        Font fuenteRoja = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.RED);
        PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteRoja));
        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
        subTabla.addCell(celdaTitulo);

        String[] datos = {
                "A beneficio de " + institucion,
                "Premio: " + premio,
                "Fecha: " + fecha, // Mostramos la fecha aquí
                "Se realizará: " + descripcion,  // Y aquí la descripción
                "Valor: $" + valorBoleto
        };

        for (String texto : datos) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
            celda.setBorder(Rectangle.NO_BORDER);
            subTabla.addCell(celda);
        }

        PdfPCell espacioFlexible = new PdfPCell(new Phrase("\n\n"));
        espacioFlexible.setBorder(Rectangle.NO_BORDER);
        subTabla.addCell(espacioFlexible);

        Font grande = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD);
        PdfPCell numeroCelda = new PdfPCell(new Phrase("N° " + numeroFormateado, grande));
        numeroCelda.setBorder(Rectangle.NO_BORDER);
        numeroCelda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        numeroCelda.setPaddingTop(5);
        subTabla.addCell(numeroCelda);

        PdfPCell contenedor = new PdfPCell(subTabla);
        contenedor.setPadding(10);
        contenedor.setBorder(Rectangle.NO_BORDER);
        return contenedor;
    }

    private static PdfPCell crearCeldaDesdeTexto(String[] lineas, Font fuente) {
        PdfPTable subTabla = new PdfPTable(1);
        for (String texto : lineas) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
            celda.setBorder(Rectangle.NO_BORDER);
            subTabla.addCell(celda);
        }
        PdfPCell contenedor = new PdfPCell(subTabla);
        contenedor.setPadding(10);
        contenedor.setBorder(Rectangle.NO_BORDER);
        return contenedor;
    }

    private static PdfPCell lineaDivisoria() {
        PdfPCell linea = new PdfPCell();
        linea.setBorder(Rectangle.LEFT);
        linea.setBorderWidthLeft(1f);
        linea.setBorderColor(BaseColor.BLACK);
        return linea;
    }
}