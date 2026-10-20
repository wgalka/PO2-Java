package pl.uczelnia.lab5;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/indicators")
public class IndicatorServlet extends HttpServlet {
    private final IndicatorDataService service = IndicatorDataService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String category = req.getParameter("category");
        List<EconomicIndicator> indicators = service.findAll(category);

        req.setAttribute("indicators", indicators);
        req.setAttribute("selectedCategory", category != null ? category : "Wszystkie");
        req.getRequestDispatcher("/WEB-INF/views/indicators.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            String code = req.getParameter("code");
            if (code != null && !code.isBlank()) {
                service.delete(code.trim());
            }
            resp.sendRedirect(req.getContextPath() + "/indicators");
            return;
        }

        // Akcja dodawania wskaźnika
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String category = req.getParameter("category");
        String unit = req.getParameter("unit");
        String valueStr = req.getParameter("value");

        String codeVal = code != null ? code.trim() : "";
        String nameVal = name != null ? name.trim() : "";
        String categoryVal = category != null ? category.trim() : "";
        String unitVal = unit != null ? unit.trim() : "";

        if (codeVal.isEmpty() || nameVal.isEmpty() || categoryVal.isEmpty() || unitVal.isEmpty()) {
            forwardWithError(req, resp, "Wszystkie pola formularza są wymagane.", codeVal, nameVal, categoryVal, unitVal, valueStr);
            return;
        }

        double val;
        try {
            val = Double.parseDouble(valueStr != null ? valueStr.trim().replace(',', '.') : "0");
        } catch (NumberFormatException e) {
            forwardWithError(req, resp, "Niepoprawny format liczby wartości wskaźnika.", codeVal, nameVal, categoryVal, unitVal, valueStr);
            return;
        }

        try {
            service.add(new EconomicIndicator(codeVal, nameVal, categoryVal, val, unitVal));
            // Wzorzec Post/Redirect/Get (PRG) zapobiega wielokrotnemu wysłaniu formularza przy odświeżeniu
            resp.sendRedirect(req.getContextPath() + "/indicators");
        } catch (IllegalArgumentException ex) {
            forwardWithError(req, resp, ex.getMessage(), codeVal, nameVal, categoryVal, unitVal, valueStr);
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error,
                                  String code, String name, String category, String unit, String value)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", error);
        req.setAttribute("formCode", code);
        req.setAttribute("formName", name);
        req.setAttribute("formCategory", category);
        req.setAttribute("formUnit", unit);
        req.setAttribute("formValue", value);

        req.setAttribute("indicators", service.findAll(null));
        req.getRequestDispatcher("/WEB-INF/views/indicators.jsp").forward(req, resp);
    }
}
