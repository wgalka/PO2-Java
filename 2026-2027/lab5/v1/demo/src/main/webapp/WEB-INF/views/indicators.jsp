<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lab 5 — Przeglądarka wskaźników ekonomicznych (JSP / Servlet)</title>
    <style>
        :root {
            color-scheme: light;
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;
            --primary: #2563eb;
            --primary-hover: #1d4ed8;
            --danger: #dc2626;
            --danger-bg: #fee2e2;
            --danger-text: #991b1b;
        }

        :root[data-theme="dark"] {
            color-scheme: dark;
            --bg: #0f172a;
            --card-bg: #1e293b;
            --text: #f8fafc;
            --muted: #94a3b8;
            --border: #334155;
            --primary: #3b82f6;
            --primary-hover: #60a5fa;
            --danger: #ef4444;
            --danger-bg: #450a0a;
            --danger-text: #fca5a5;
        }

        @media (prefers-color-scheme: dark) {
            :root:not([data-theme]) {
                color-scheme: dark;
                --bg: #0f172a;
                --card-bg: #1e293b;
                --text: #f8fafc;
                --muted: #94a3b8;
                --border: #334155;
                --primary: #3b82f6;
                --primary-hover: #60a5fa;
                --danger: #ef4444;
                --danger-bg: #450a0a;
                --danger-text: #fca5a5;
            }
        }

        body {
            font-family: system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            margin: 0;
            padding: 1.5rem;
            line-height: 1.5;
        }

        .container {
            max-width: 900px;
            margin: 0 auto;
        }

        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            flex-wrap: wrap;
            gap: 1rem;
        }

        h1 { margin: 0; font-size: 1.5rem; }

        .card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 1.25rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }

        .alert-error {
            background: var(--danger-bg);
            color: var(--danger-text);
            border: 1px solid var(--danger);
            padding: 0.75rem 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        th, td {
            text-align: left;
            padding: 0.75rem;
            border-bottom: 1px solid var(--border);
        }

        th {
            color: var(--muted);
            font-size: 0.875rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 1rem;
            align-items: end;
        }

        label {
            display: flex;
            flex-direction: column;
            font-size: 0.875rem;
            gap: 0.35rem;
            font-weight: 500;
        }

        input, select, button {
            font: inherit;
            padding: 0.5rem 0.75rem;
            border-radius: 6px;
            border: 1px solid var(--border);
            background: var(--card-bg);
            color: var(--text);
        }

        button.btn-primary {
            background: var(--primary);
            color: #ffffff;
            border: none;
            cursor: pointer;
            font-weight: 600;
        }
        button.btn-primary:hover { background: var(--primary-hover); }

        button.btn-delete {
            background: transparent;
            color: var(--danger);
            border: 1px solid var(--danger);
            cursor: pointer;
            padding: 0.25rem 0.5rem;
            font-size: 0.8rem;
        }

        .theme-control { min-width: 9rem; }
    </style>
</head>
<body>
<div class="container">
    <header>
        <div>
            <h1>Główne wskaźniki makroekonomiczne</h1>
            <small style="color: var(--muted)">Model MVC w Jakarta Servlet API i JSP</small>
        </div>
        <label class="theme-control" for="themeMode">Wygląd
            <select id="themeMode" name="themeMode">
                <option value="system">Zgodnie z systemem</option>
                <option value="light">Jasny</option>
                <option value="dark">Ciemny</option>
            </select>
        </label>
    </header>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error" role="alert">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <section class="card">
        <form method="get" action="${pageContext.request.contextPath}/indicators" style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
            <label style="flex-direction: row; align-items: center;">
                Kategoria:
                <select name="category" onchange="this.form.submit()">
                    <option value="Wszystkie" <c:if test="${selectedCategory == 'Wszystkie'}">selected</c:if>>Wszystkie</option>
                    <option value="Rynek pracy" <c:if test="${selectedCategory == 'Rynek pracy'}">selected</c:if>>Rynek pracy</option>
                    <option value="Ceny" <c:if test="${selectedCategory == 'Ceny'}">selected</c:if>>Ceny</option>
                    <option value="Rachunki narodowe" <c:if test="${selectedCategory == 'Rachunki narodowe'}">selected</c:if>>Rachunki narodowe</option>
                    <option value="Handel" <c:if test="${selectedCategory == 'Handel'}">selected</c:if>>Handel</option>
                </select>
            </label>
            <noscript><button type="submit">Filtruj</button></noscript>
        </form>

        <div style="overflow-x: auto;">
            <table>
                <thead>
                <tr>
                    <th>Kod</th>
                    <th>Nazwa wskaźnika</th>
                    <th>Kategoria</th>
                    <th>Wartość</th>
                    <th>Jednostka</th>
                    <th>Akcja</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${indicators}">
                    <tr>
                        <td><strong><c:out value="${item.code()}"/></strong></td>
                        <td><c:out value="${item.name()}"/></td>
                        <td><c:out value="${item.category()}"/></td>
                        <td><c:out value="${item.latestValue()}"/></td>
                        <td><c:out value="${item.unit()}"/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/indicators" style="margin:0;">
                                <input type="hidden" name="action" value="delete"/>
                                <input type="hidden" name="code" value="<c:out value="${item.code()}"/>"/>
                                <button type="submit" class="btn-delete" onclick="return confirm('Czy na pewno usunąć wskaźnik?')">Usuń</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty indicators}">
                    <tr>
                        <td colspan="6" style="text-align: center; color: var(--muted); padding: 1.5rem;">
                            Brak wskaźników dla wybranego kryterium.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>

    <section class="card">
        <h2 style="margin-top:0; font-size: 1.15rem;">Rejestracja nowego wskaźnika</h2>
        <form method="post" action="${pageContext.request.contextPath}/indicators" class="form-grid">
            <input type="hidden" name="action" value="add"/>
            <label>
                Kod wskaźnika:
                <input type="text" name="code" value="<c:out value="${formCode}"/>" placeholder="np. WIBOR_3M" required maxlength="30"/>
            </label>
            <label>
                Nazwa wskaźnika:
                <input type="text" name="name" value="<c:out value="${formName}"/>" placeholder="np. Stopa referencyjna" required maxlength="120"/>
            </label>
            <label>
                Kategoria:
                <select name="category" required>
                    <option value="Rynek pracy">Rynek pracy</option>
                    <option value="Ceny">Ceny</option>
                    <option value="Rachunki narodowe">Rachunki narodowe</option>
                    <option value="Handel">Handel</option>
                    <option value="Rynki finansowe">Rynki finansowe</option>
                </select>
            </label>
            <label>
                Wartość bieżąca:
                <input type="text" name="value" value="<c:out value="${formValue}"/>" placeholder="np. 5.85" required/>
            </label>
            <label>
                Jednostka:
                <input type="text" name="unit" value="<c:out value="${formUnit}"/>" placeholder="np. %" required maxlength="20"/>
            </label>
            <button type="submit" class="btn-primary">Dodaj wskaźnik (POST)</button>
        </form>
    </section>
</div>

<script>
    const root = document.documentElement;
    const themeMode = document.getElementById('themeMode');
    const allowedModes = ['system', 'light', 'dark'];
    const savedMode = localStorage.getItem('themeMode');

    function applyThemeMode(mode) {
        const selectedMode = allowedModes.includes(mode) ? mode : 'system';
        if (selectedMode === 'system') {
            root.removeAttribute('data-theme');
        } else {
            root.dataset.theme = selectedMode;
        }
        themeMode.value = selectedMode;
        localStorage.setItem('themeMode', selectedMode);
    }

    applyThemeMode(savedMode || 'system');
    themeMode.addEventListener('change', () => applyThemeMode(themeMode.value));
</script>
</body>
</html>
