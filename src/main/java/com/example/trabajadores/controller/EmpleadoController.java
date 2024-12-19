/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.trabajadores.controller;

import com.example.trabajadores.service.EmpleadoService;
import com.example.trabajadores.model.Empleado;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletResponse;

// importaciones pdf
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

// importaciones excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author sise
 */
@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.service = empleadoService;
    }

    @GetMapping
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", this.service.listarTodas());
        return "empleados";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "formulario";
    }

    @PostMapping
    public String guardarEmpleado(@ModelAttribute Empleado empleado) {
        this.service.guardar(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("empleado", this.service.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("ID invalido" + id)));
        return "formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        this.service.eliminar(id);
        return "redirect:/empleados";
    }

    @DeleteMapping("/{id}")
    public String eliminarPersonas(@PathVariable Long id) {
        this.service.eliminar(id);
        return "redirect:/empleados";
    }

    @GetMapping("/reporte/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=empleados_reporte.pdf");
        //        PdfWriter write = new PdfWriter(response.getOutputStream());
        //        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(write));
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDocument);

// Título del reporte
        document.add(new Paragraph("Reporte de Empleados").setBold().setFontSize(9).setTextAlignment(TextAlignment.CENTER));

        // Ajustes de márgenes y tamaño de fuente 
        document.setMargins(20, 20, 20, 20);
        document.setFontSize(9);

        // Tabla con 10 columnas
        Table table = new Table(11);
        table.addCell("ID");
        table.addCell("Nombre");
        table.addCell("Apellido Paterno");
        table.addCell("Apellido Materno");
        table.addCell("Numero de Documento");
        table.addCell("Fecha de Nacimiento");
        table.addCell("Direccion");
        table.addCell("Meses Trabajados");
        table.addCell("Tipo de Seguro");
        table.addCell("Sueldo");
        table.addCell("Telefono");

        // Obtener los empleados y añadir sus datos
        List<Empleado> empleados = this.service.listarTodas();
        DecimalFormat df = new DecimalFormat("#,##0.00"); // Instanciamos el formateador

        for (Empleado empleado : empleados) {
            table.addCell(empleado.getId().toString());
            table.addCell(empleado.getNombre());
            table.addCell(empleado.getApellido_paterno());
            table.addCell(empleado.getApellido_materno());
            table.addCell(empleado.getNumero_documento());
            table.addCell(empleado.getFecha_nacimiento().toString());
            table.addCell(empleado.getDireccion());
            table.addCell(String.valueOf(empleado.getMeses_trabajados()));
            table.addCell(empleado.getTipo_seguro());

            // Formatear el sueldo antes de agregarlo al PDF
            String formattedSueldo = df.format(empleado.getSueldo());
            table.addCell(formattedSueldo);
            table.addCell(empleado.getTelefono());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/reporte/excel")
    public void generarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=empleados_reporte.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Empleados");

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"ID", "Nombre", "Apellidos"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        List<Empleado> empleados = this.service.listarTodas();
        int rowIndex = 1;
        for (Empleado empleado : empleados) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(empleado.getId());
            row.createCell(1).setCellValue(empleado.getNombre());
            row.createCell(2).setCellValue(empleado.getApellido_paterno());
            row.createCell(3).setCellValue(empleado.getApellido_materno());
            row.createCell(4).setCellValue(empleado.getNumero_documento());
            row.createCell(5).setCellValue(empleado.getFecha_nacimiento());
            row.createCell(6).setCellValue(empleado.getDireccion());
            row.createCell(7).setCellValue(empleado.getMeses_trabajados());
            row.createCell(8).setCellValue(empleado.getTipo_seguro());
            row.createCell(9).setCellValue(String.format("%.2f", empleado.getSueldo()));
              row.createCell(10).setCellValue(empleado.getTelefono());
        }

        /*for (int i = 0; columnHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        workbook.write(response.getOutputStream());
        workbook.close();

    }

}
