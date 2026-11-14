// Página genérica de módulo: modulo.html?m=<id>. Los datos viven en el servidor Java
// (clase ServidorWeb, puerto 8080): aquí solo se piden con fetch y se dibujan.
(() => {
  const ESTADOS = [["1", "Activo"], ["2", "Inactivo"]];
  const NIVELES = [["1", "Técnico"], ["2", "Tecnológico"], ["3", "Pregrado"],
                   ["4", "Especialización"], ["5", "Maestría"], ["6", "Doctorado"]];
  const TIPOS = [["1", "Profesor principal"], ["2", "Profesor acompañante"], ["3", "Auditor"]];
  const FILTRO_ESTADO = { k: "estado", l: "Estado", op: [["1", "Activos"], ["2", "Inactivos"]] };

  // k = clave interna, l = etiqueta del formulario, c = encabezado de la tabla,
  // fijo = no se puede cambiar al actualizar, estados:null = el módulo no tiene estado.
  const ESQUEMAS = {
    profesores: { singular: "Profesor", plural: "profesores", etiqueta: r => r.nombre,
      filtros: [FILTRO_ESTADO], campos: [
      { k: "nombre", l: "Ingrese el nombre", c: "Nombre" },
      { k: "celular", l: "Celular de contacto", c: "Celular", t: "tel" }] },
    estudiantes: { singular: "Estudiante", plural: "estudiantes", idCampo: "codigo", filtros: [FILTRO_ESTADO],
      etiqueta: r => `${r.codigo} - ${r.nombre}`, campos: [
      { k: "codigo", l: "Digite el código del estudiante", c: "Código", fijo: 1 },
      { k: "nombre", l: "Ingrese el nombre", c: "Nombre", fijo: 1 },
      { k: "telefono", l: "Teléfono de contacto", c: "Teléfono", t: "tel" },
      { k: "correo", l: "Correo del estudiante", c: "Correo", t: "email" },
      { k: "direccion", l: "Dirección", c: "Dirección" }] },
    facultades: { singular: "Facultad", plural: "facultades", etiqueta: r => r.nombre, campos: [
      { k: "nombre", l: "Nombre de la facultad", c: "Nombre" }] },
    programas: { singular: "Programa", plural: "programas", etiqueta: r => r.nombre,
      filtros: [{ k: "facultad", l: "Facultad", ref: "facultades" }], campos: [
      { k: "nombre", l: "Nombre del programa", c: "Nombre" },
      { k: "nivel", l: "Nivel del programa", c: "Nivel", t: "select", op: NIVELES },
      { k: "facultad", l: "Facultad a la que pertenece", c: "Facultad", t: "ref", ref: "facultades" }] },
    planes: { singular: "Plan de estudio", plural: "planes de estudio",
      etiqueta: r => `${r.version} (${etiquetaDe("programas", r.programa)})`,
      estados: [["1", "Vigente"], ["2", "No vigente"]],
      filtros: [{ k: "programa", l: "Programa", ref: "programas" }], campos: [
      { k: "version", l: "Versión del plan", c: "Versión" },
      { k: "programa", l: "Programa", c: "Programa", t: "ref", ref: "programas", fijo: 1 }] },
    materias: { singular: "Materia", plural: "materias", etiqueta: r => r.nombre, campos: [
      { k: "nombre", l: "Nombre de la materia", c: "Nombre" },
      { k: "creditos", l: "Créditos (1 a 6)", c: "Créditos", t: "number", min: 1, max: 6 },
      { k: "cupo", l: "Cupo máximo (1 a 50)", c: "Cupo máximo", t: "number", min: 1, max: 50 }] },
    periodos: { singular: "Período académico", plural: "períodos académicos", etiqueta: r => r.nombre, campos: [
      { k: "nombre", l: "Nombre del período (por ejemplo 2026-1)", c: "Nombre" }] },
    cursos: { singular: "Curso por período", plural: "cursos por período", etiqueta: r => r.nombre, campos: [
      { k: "nombre", l: "Nombre del curso", c: "Nombre" },
      { k: "materia", l: "Materia", c: "Materia", t: "ref", ref: "materias", fijo: 1 },
      { k: "periodo", l: "Período académico", c: "Período", t: "ref", ref: "periodos", fijo: 1 },
      { k: "cupo", l: "Cupo del curso", c: "Cupo", t: "number", min: 1 }],
      estados: null,
      validar: d => {
        const mat = leer("materias").find(r => r.id === d.materia);
        if (mat && Number(d.cupo) > Number(mat.cupo)) return `El cupo no puede superar el cupo máximo de la materia (${mat.cupo}).`;
      } },
    asignacion: { singular: "Asignación de profesor", plural: "asignaciones de profesores",
      etiqueta: r => `${etiquetaDe("profesores", r.profesor)} en ${etiquetaDe("cursos", r.curso)}`,
      estados: [["1", "Asignado"], ["2", "Retirado"]],
      filtros: [{ k: "profesor", l: "Profesor", ref: "profesores" }, { k: "materia", l: "Materia", ref: "materias" },
                { k: "periodo", l: "Período", ref: "periodos" }], campos: [
      { k: "curso", l: "Curso", c: "Curso", t: "ref", ref: "cursos", fijo: 1 },
      { k: "profesor", l: "Profesor", c: "Profesor", t: "ref", ref: "profesores", fijo: 1 },
      { k: "tipo", l: "Tipo de asignación", c: "Tipo", t: "select", op: TIPOS }],
      validar: (d, otros, estado) => {
        const prof = leer("profesores").find(r => r.id === d.profesor);
        if (otros.some(r => r.curso === d.curso && r.profesor === d.profesor)) return "Ese profesor ya está asignado a este curso. Use Actualizar para reactivarlo.";
        if (estado === "2") return;
        if (prof && prof.estado !== "1") return "El profesor está inactivo y no puede asignarse.";
        if (d.tipo === "1" && otros.some(r => r.curso === d.curso && r.tipo === "1" && r.estado === "1")) return "Este curso ya tiene un profesor principal.";
      } },
    formacion: { singular: "Formación de profesor", plural: "formaciones de profesores", estados: null,
      filtros: [{ k: "profesor", l: "Profesor", ref: "profesores" }],
      etiqueta: r => `${etiquetaDe("profesores", r.profesor)} - ${r.titulo}`, campos: [
      { k: "profesor", l: "Profesor", c: "Profesor", t: "ref", ref: "profesores", fijo: 1 },
      { k: "titulo", l: "Título de la formación", c: "Título" },
      { k: "fecha", l: "Fecha de la formación", c: "Fecha", t: "date" }] }
  };

  /* ---------- Utilidades ---------- */
  const h = (tag, attrs = {}, ...hijos) => {
    const e = document.createElement(tag);
    for (const [n, v] of Object.entries(attrs)) {
      if (v === false || v == null) continue;
      e.setAttribute(n, v === true ? "" : v);
    }
    e.append(...hijos);
    return e;
  };
  const estadosDe = e => ("estados" in e ? e.estados : ESTADOS);

  const API = "http://localhost:8080/api";
  const almacen = {}; // copia en memoria de lo que devolvió el servidor

  function leer(m) { return almacen[m] || []; }

  // GET al servidor. consulta = filtros opcionales (?estado=1). Devuelve la lista o el registro pedido.
  async function pedir(ruta, consulta = {}) {
    const q = new URLSearchParams(Object.entries(consulta).filter(([, v]) => v)).toString();
    let r;
    try {
      r = await fetch(`${API}/${ruta}${q ? "?" + q : ""}`);
    } catch {
      throw new Error("No se pudo conectar con el servidor Java.");
    }
    const cuerpo = await r.json();
    if (!r.ok) throw new Error(cuerpo.error || "Error del servidor");
    return cuerpo;
  }

  async function cargarTodo() {
    await Promise.all(Object.keys(ESQUEMAS).map(async m => { almacen[m] = await pedir(m); }));
  }

  const norm = t => t.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

  // metodo: POST (crear) o PUT (actualizar). Los campos viajan como formulario.
  async function enviar(metodo, ruta, campos) {
    let r;
    try {
      r = await fetch(`${API}/${ruta}`, { method: metodo, body: new URLSearchParams(campos) });
    } catch {
      throw new Error("No se pudo conectar con el servidor Java.");
    }
    const cuerpo = await r.json();
    if (!r.ok) throw new Error(cuerpo.error || "Error del servidor");
  }

  function etiquetaDe(m, id) {
    const r = leer(m).find(x => x.id === id);
    return r ? ESQUEMAS[m].etiqueta(r) : "(eliminado)";
  }
  function opcionesRef(m, todos) {
    const e = ESQUEMAS[m];
    return leer(m).filter(r => todos || !estadosDe(e) || r.estado === "1").map(r => [r.id, e.etiqueta(r)]);
  }
  function textoDe(c, v) {
    if (v == null || v === "") return "";
    if (c.t === "ref") return etiquetaDe(c.ref, v);
    if (c.t === "select") return (c.op.find(([x]) => x === v) || [])[1] || v;
    if (c.t === "date") return v.split("-").reverse().join("/");
    return v;
  }

  /* ---------- Controles de formulario ---------- */
  function crearControl(c, valor, modo) {
    const id = "f-" + c.k;
    let ctl;
    if (c.t === "select" || c.t === "ref") {
      const ops = c.t === "ref" ? opcionesRef(c.ref, modo === "editar") : c.op;
      ctl = h("select", { id, name: c.k, required: true },
        ...(c.t === "ref" ? [h("option", { value: "" }, "Seleccione…")] : []),
        ...ops.map(([v, t]) => h("option", { value: v }, t)));
    } else {
      ctl = h("input", { id, name: c.k, type: c.t || "text", required: true, min: c.min, max: c.max,
        minlength: c.t === "tel" ? 7 : null, inputmode: c.t === "tel" ? "tel" : null, autocomplete: "off" });
    }
    if (valor != null) ctl.value = valor;
    if (modo === "editar" && c.fijo) ctl.disabled = true;
    return h("div", { class: "campo" }, h("label", { for: id }, c.l), ctl);
  }

  function controlEstado(estados, valor) {
    const ctl = h("select", { id: "f-estado", name: "estado" }, ...estados.map(([v, t]) => h("option", { value: v }, t)));
    ctl.value = valor;
    return h("div", { class: "campo" }, h("label", { for: "f-estado" }, "Estado"), ctl);
  }

  const leerDatos = (form, campos) =>
    Object.fromEntries(campos.map(c => [c.k, form.elements[c.k].value.trim()]));

  function validar(esq, d, lista, actual, estado) {
    if (Object.values(d).some(v => !v)) return "Complete todos los campos.";
    const otros = lista.filter(r => !actual || r.id !== actual.id);
    if (esq.idCampo && otros.some(r => r[esq.idCampo] === d[esq.idCampo])) return "Ya existe un registro con ese código.";
    return (esq.validar && esq.validar(d, otros, estado)) || "";
  }

  /* ---------- Vistas ---------- */
  function vistaCrear(m, esq) {
    const estados = estadosDe(esq);
    const form = h("form", { class: "formulario" });
    const error = h("p", { class: "error", role: "alert" });
    esq.campos.forEach(c => form.append(crearControl(c, null, "crear")));
    form.append(error, h("button", { class: "btn-accion", type: "submit" }, "Registrar"));

    const falta = esq.campos.find(c => c.t === "ref" && opcionesRef(c.ref, false).length === 0);
    if (falta) {
      const destino = ESQUEMAS[falta.ref];
      error.append(`Primero registre al menos un elemento en `,
        h("a", { href: `modulos.html?m=${falta.ref}` }, destino.plural), ".");
    }

    form.addEventListener("submit", async e => {
      e.preventDefault();
      error.textContent = "";
      const valores = leerDatos(form, esq.campos);
      const estado = estados ? estados[0][0] : undefined;
      const msg = validar(esq, valores, leer(m), null, estado);
      if (msg) { error.textContent = msg; return; }
      try {
        await enviar("POST", m, valores);
        await cargarTodo();
        mostrarAviso(`${esq.singular} registrado correctamente.`);
        form.reset();
        form.elements[esq.campos[0].k].focus();
      } catch (err) {
        error.textContent = err.message;
      }
    });
    return form;
  }

  function vistaListar(m, esq) {
    const estados = estadosDe(esq);
    const todos = leer(m);
    const conId = !esq.idCampo && todos.every(r => /^\d+$/.test(r.id));
    const cols = conId ? [{ c: "Código", v: r => r.id }] : [];
    esq.campos.forEach(c => cols.push({ c: c.c, v: r => textoDe(c, r[c.k]) }));
    if (estados) cols.push({ c: "Estado", v: r => (estados.find(([x]) => x === r.estado) || [])[1] || "" });

    let filas = todos; // lo que devolvio el servidor segun los filtros elegidos
    const resumen = h("p", { class: "resumen" });
    const zona = h("div", { class: "tabla-wrap", tabindex: "0", role: "region", "aria-label": `Lista de ${esq.plural}` });
    const buscador = h("input", { type: "search", id: "f-Buscar por ID", placeholder: "Ingrese la ID correspondiente…", autocomplete: "off" });

    // Cada filtro es un selector; al cambiarlo se vuelve a pedir la lista al servidor
    const filtros = (esq.filtros || []).map(f => {
      const ops = f.ref ? opcionesRef(f.ref, true) : f.op;
      const sel = h("select", { id: "f-filtro-" + f.k }, h("option", { value: "" }, "Todos"),
        ...ops.map(([v, t]) => h("option", { value: v }, t)));
      sel.addEventListener("change", recargar);
      return { k: f.k, sel, caja: h("div", { class: "campo" }, h("label", { for: sel.id }, f.l), sel) };
    });

    function pintar() {
      const tabla = h("table", {},
        h("thead", {}, h("tr", {}, ...cols.map(col => h("th", { scope: "col" }, col.c)))),
        h("tbody", {},
          ...filas.map(r => h("tr", { "data-fila": "1" }, ...cols.map(col => h("td", {}, String(col.v(r)))))),
          h("tr", { "data-vacio": "1" }, h("td", { colspan: cols.length, class: "vacio" }))));
      tabla.style.setProperty("--cols", cols.length);
      zona.replaceChildren(tabla);
      aplicarBusqueda();
    }

    // La busqueda de texto solo oculta filas ya cargadas (no consulta al servidor)
    function aplicarBusqueda() {
      const q = norm(buscador.value.trim());
      let visibles = 0;
      for (const tr of zona.querySelectorAll("tr[data-fila]")) {
        tr.hidden = !!q && !norm(tr.textContent).includes(q);
        if (!tr.hidden) visibles++;
      }
      const vacio = zona.querySelector("tr[data-vacio]");
      vacio.hidden = visibles > 0;
      vacio.firstChild.textContent = filas.length === 0
        ? (filtros.some(f => f.sel.value)
            ? "No hay registros con ese filtro."
            : `Aún no hay ${esq.plural} registrados. Use «Crear ${esq.singular.toLowerCase()}» para agregar el primero.`)
        : "Ningún registro coincide con la búsqueda.";
      resumen.textContent = visibles === todos.length
        ? `Total de ${esq.plural}: ${visibles}`
        : `Mostrando ${visibles} de ${todos.length} ${esq.plural}`;
    }

    async function recargar() {
      try {
        filas = await pedir(m, Object.fromEntries(filtros.map(f => [f.k, f.sel.value])));
        pintar();
      } catch (err) {
        zona.replaceChildren(h("p", { class: "error" }, err.message));
      }
    }

    buscador.addEventListener("input", aplicarBusqueda);
    pintar();
    const barra = h("div", { class: "filtros" },
      h("div", { class: "campo" }, h("label", { for: "f-buscar" }, "Buscar"), buscador),
      ...filtros.map(f => f.caja));
    return h("div", {}, barra, resumen, zona);
  }

  function vistaActualizar(m, esq) {
    const lista = leer(m);
    if (!lista.length) {
      return h("p", { class: "vacio" }, `Aún no hay ${esq.plural} para actualizar.`);
    }
    const sel = h("select", { id: "f-registro" }, h("option", { value: "" }, "Seleccione…"),
      ...lista.map(r => h("option", { value: r.id }, esq.etiqueta(r))));
    const zona = h("div");
    sel.addEventListener("change", async () => {
      zona.replaceChildren();
      if (!sel.value) return;
      try {
        // el servidor responde con los datos actuales del registro (buscarPorId)
        zona.append(formEditar(m, esq, await pedir(`${m}/${encodeURIComponent(sel.value)}`)));
      } catch (err) {
        zona.append(h("p", { class: "error" }, err.message));
      }
    });
    return h("div", { class: "formulario" },
      h("div", { class: "campo" }, h("label", { for: "f-registro" }, `Elija el ${esq.singular.toLowerCase()} que desea actualizar`), sel), zona);
  }

  function formEditar(m, esq, reg) {
    const estados = estadosDe(esq);
    const form = h("form", { class: "formulario" });
    const error = h("p", { class: "error", role: "alert" });
    esq.campos.forEach(c => form.append(crearControl(c, reg[c.k], "editar")));
    if (estados) form.append(controlEstado(estados, reg.estado));
    form.append(error, h("button", { class: "btn-accion", type: "submit" }, "Guardar cambios"));

    form.addEventListener("submit", async e => {
      e.preventDefault();
      error.textContent = "";
      const valores = leerDatos(form, esq.campos);
      const estado = estados ? form.elements.estado.value : undefined;
      const actual = leer(m).find(r => r.id === reg.id);
      const msg = validar(esq, valores, leer(m), actual, estado);
      if (msg) { error.textContent = msg; return; }
      try {
        await enviar("PUT", `${m}/${encodeURIComponent(reg.id)}`, estado ? { ...valores, estado } : valores);
        await cargarTodo();
        mostrarAviso(`${esq.singular} actualizado correctamente.`);
        mostrar("actualizar");
      } catch (err) {
        error.textContent = err.message;
      }
    });
    return form;
  }

  /* ---------- Arranque ---------- */
  const m = new URLSearchParams(location.search).get("m");
  const esq = ESQUEMAS[m];
  const acciones = document.getElementById("acciones");
  const panel = document.getElementById("panel");

  if (!esq) {
    acciones.hidden = true;
    panel.append(h("p", { class: "vacio" }, "No se encontró ese módulo. ", h("a", { href: "index.html" }, "Volver al inicio")));
    return;
  }

  const nombre = esq.singular.toLowerCase();
  document.title = `Módulo ${esq.singular} - Unimagdalena`;
  document.getElementById("titulo-pagina").textContent = `Módulo ${esq.singular}`;

  const vistas = {
    crear: [`Crear ${nombre}`, () => vistaCrear(m, esq)],
    listar: [`Listar ${esq.plural}`, () => vistaListar(m, esq)],
    actualizar: [`Actualizar ${nombre}`, () => vistaActualizar(m, esq)]
  };
  const botones = {};

  function mostrar(vista) {
    for (const [clave, b] of Object.entries(botones)) b.setAttribute("aria-pressed", String(clave === vista));
    panel.classList.toggle("panel--tabla", vista === "listar");
    panel.replaceChildren(vistas[vista][1]());
  }

  for (const [clave, [texto]] of Object.entries(vistas)) {
    botones[clave] = h("button", { type: "button", class: "pestana", "aria-pressed": "false" }, texto);
    botones[clave].addEventListener("click", () => mostrar(clave));
    acciones.append(botones[clave]);
  }
  cargarTodo()
    .then(() => mostrar("crear"))
    .catch(() => {
      acciones.hidden = true;
      panel.append(h("p", { class: "error" },
        "No se pudo conectar con el servidor Java. Ejecute ServidorWeb (puerto 8080) y recargue la página."));
    });
})();