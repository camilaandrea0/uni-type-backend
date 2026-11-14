// Módulos del proyecto. Cada uno abre modulo.html?m=<id>, que arma el módulo
// a partir de su esquema en modulo.js (el id debe coincidir con ese esquema).
const MODULOS = [
  { id: "profesores",  titulo: "Módulo de Profesores",
    descripcion: "Ingrese nuevos profesores a la base de datos, desactive o liste los profesores activos.", url: "modulos.html?m=profesores" },
  { id: "estudiantes", titulo: "Módulo de Estudiantes",
    descripcion: "Ingrese nuevos estudiantes a la base de datos, actualice sus datos o liste los estudiantes activos.", url: "modulos.html?m=estudiantes" },
  { id: "facultades",  titulo: "Módulo de Facultades",
    descripcion: "Registre las facultades de la universidad, cambie su nombre o desactívelas.", url: "modulos.html?m=facultades" },
  { id: "programas",   titulo: "Módulo de Programas",
    descripcion: "Cree programas académicos por nivel y asócielos a su facultad.", url: "modulos.html?m=programas" },
  { id: "planes",      titulo: "Módulo de Planes de estudio",
    descripcion: "Gestione las versiones del plan de estudio de cada programa.", url: "modulos.html?m=planes" },
  { id: "materias",    titulo: "Módulo de Materias",
    descripcion: "Defina las materias con sus créditos y su cupo máximo.", url: "modulos.html?m=materias" },
  { id: "periodos",    titulo: "Módulo de Períodos académicos",
    descripcion: "Abra, renombre o desactive los períodos académicos.", url: "modulos.html?m=periodos" },
  { id: "cursos",      titulo: "Módulo de Cursos por período",
    descripcion: "Programe los cursos de una materia en un período y defina su cupo.", url: "modulos.html?m=cursos" },
  { id: "asignacion",  titulo: "Módulo de Asignación de profesores",
    descripcion: "Asigne profesores a los cursos como principal, acompañante o auditor, y retírelos o reactívelos.", url: "modulos.html?m=asignacion" },
  { id: "formacion",   titulo: "Módulo de Formación de profesores",
    descripcion: "Registre la formación académica de cada profesor.", url: "modulos.html?m=formacion" }
];

const contenedor = document.getElementById("modulos");
const listaMenu = document.getElementById("menu-lista");
const botonMenu = document.querySelector(".menu-btn");
const menu = document.getElementById("menu");
const aviso = document.getElementById("aviso");
let temporizadorAviso;

/* ---------- Tarjetas y menú ---------- */
function crearTarjeta(modulo, indice) {
  const tarjeta = document.createElement("article");
  tarjeta.className = "modulo " + (indice % 2 === 0 ? "v1" : "v2");
  tarjeta.id = "modulo-" + modulo.id;

  const titulo = document.createElement("h2");
  titulo.id = "titulo-" + modulo.id;
  titulo.textContent = modulo.titulo;

  const texto = document.createElement("p");
  texto.textContent = modulo.descripcion;

  const boton = document.createElement("button");
  boton.type = "button";
  boton.className = "acceder";
  boton.textContent = "Acceder al módulo";
  boton.setAttribute("aria-describedby", titulo.id);
  boton.addEventListener("click", () => abrirModulo(modulo));

  tarjeta.append(titulo, texto, boton);
  return tarjeta;
}

function crearItemMenu(modulo) {
  const item = document.createElement("li");
  const enlace = document.createElement("a");
  enlace.href = modulo.url;
  enlace.textContent = modulo.titulo.replace("Módulo de ", "");
  enlace.addEventListener("click", () => cambiarMenu(false));
  item.append(enlace);
  return item;
}

MODULOS.forEach((modulo, i) => {
  if (contenedor) contenedor.append(crearTarjeta(modulo, i));
  listaMenu.append(crearItemMenu(modulo));
});

const itemInicio = document.createElement("li");
const enlaceInicio = document.createElement("a");
enlaceInicio.href = "index.html";
enlaceInicio.textContent = "Inicio";
itemInicio.append(enlaceInicio);
listaMenu.prepend(itemInicio);

/* ---------- Acciones ---------- */
function abrirModulo(modulo) {
  if (modulo.url && modulo.url !== "#") {
    window.location.href = modulo.url;
    return;
  }
  mostrarAviso("Abriendo el " + modulo.titulo.toLowerCase() + "…");
}

function mostrarAviso(mensaje) {
  aviso.textContent = mensaje;
  aviso.classList.add("visible");
  clearTimeout(temporizadorAviso);
  temporizadorAviso = setTimeout(() => aviso.classList.remove("visible"), 2400);
}

function cambiarMenu(abrir) {
  botonMenu.setAttribute("aria-expanded", String(abrir));
  botonMenu.setAttribute("aria-label", abrir ? "Cerrar menú de módulos" : "Abrir menú de módulos");
  menu.hidden = !abrir;
}

botonMenu.addEventListener("click", () => {
  cambiarMenu(botonMenu.getAttribute("aria-expanded") !== "true");
});

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape" && !menu.hidden) {
    cambiarMenu(false);
    botonMenu.focus();
  }
});

document.addEventListener("click", (e) => {
  if (!menu.hidden && !menu.contains(e.target) && !botonMenu.contains(e.target)) cambiarMenu(false);
});

/* ---------- Ondas naranjas decorativas ---------- */
function crearOndas(clase) {
  const ns = "http://www.w3.org/2000/svg";
  const svg = document.createElementNS(ns, "svg");
  svg.setAttribute("class", "ondas " + clase);
  svg.setAttribute("viewBox", "0 0 1000 1000");
  svg.setAttribute("preserveAspectRatio", "none");

  for (let i = 0; i < 38; i++) {
    const d = i * 7;
    const ruta = document.createElementNS(ns, "path");
    ruta.setAttribute("d",
      `M0 ${620 + d} C 250 ${300 + d * 0.6}, 450 ${950 - d * 0.4}, 1000 ${380 + d}`);
    svg.append(ruta);
  }
  return svg;
}

const decoracion = document.querySelector(".decoracion");
decoracion.prepend(crearOndas("ondas--abajo"), crearOndas("ondas--arriba"));