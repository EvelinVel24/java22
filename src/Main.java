import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Main {
    private static final String PRODUCTOS_FILE = "../data/productos.xlsx";
    private static final String COMPROBANTE_FILE = "../output/comprobante.txt";
    private static Map<String, Producto> productos = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Cargar productos desde el archivo Excel
        cargarProductos();

        List<Producto> carrito = new ArrayList<>();
        double total = 0.0;
        boolean salir = false;

        // Menú de ventas
        while (!salir) {
            mostrarMenu();
            System.out.print("Selecciona una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    mostrarProductos();
                    break;
                case "2":
                    Producto producto = seleccionarProducto();
                    if (producto != null) {
                        carrito.add(producto);
                        total += producto.getPrecio();
                        System.out.println(producto.getNombre() + " agregado al carrito.");
                    }
                    break;
                case "3":
                    generarComprobante(carrito, total);
                    salir = true; // Salir después de generar el comprobante
                    break;
                default:
                    System.out.println("Opción no válida, intenta de nuevo.");
            }
        }
    }

    // Cargar productos desde el archivo Excel
    public static void cargarProductos() {
        try (FileInputStream fis = new FileInputStream(PRODUCTOS_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar la fila de encabezado
                String codigo = row.getCell(0).getStringCellValue();
                String nombre = row.getCell(1).getStringCellValue();
                double precio = row.getCell(2).getNumericCellValue();

                productos.put(codigo, new Producto(codigo, nombre, precio));
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de productos.");
        }
    }

    // Mostrar el menú
    public static void mostrarMenu() {
        System.out.println("\nMENÚ:");
        System.out.println("1. Ver productos");
        System.out.println("2. Agregar producto al carrito");
        System.out.println("3. Finalizar venta y generar comprobante");
    }

    // Mostrar la lista de productos
    public static void mostrarProductos() {
        System.out.println("\nLISTA DE PRODUCTOS:");
        for (Producto producto : productos.values()) {
            System.out.println(producto);
        }
    }

    // Seleccionar un producto por código
    public static Producto seleccionarProducto() {
        System.out.print("Ingresa el código del producto: ");
        String codigo = scanner.nextLine();

        Producto producto = productos.get(codigo);
        if (producto == null) {
            System.out.println("Producto no encontrado.");
        }
        return producto;
    }

    // Generar el comprobante de venta
    public static void generarComprobante(List<Producto> carrito, double total) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMPROBANTE_FILE))) {
            writer.write("COMPROBANTE DE VENTA\n");
            writer.write("----------------------------\n");
            for (Producto producto : carrito) {
                writer.write(producto.getNombre() + " - $" + producto.getPrecio() + "\n");
            }
            writer.write("----------------------------\n");
            writer.write("Total: $" + total + "\n");
            System.out.println("Comprobante generado con éxito.");
        } catch (IOException e) {
            System.out.println("Error al generar el comprobante.");
        }
    }
}

class Producto {
    private String codigo;
    private String nombre;
    private double precio;

    public Producto(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " - $" + precio;
    }
}
