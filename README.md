# Tecsecurity - [Proyecto 2023-II]

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Logo.jpg">
</p>

## Definición del proyecto:

El reto consta de cuatro pantallas, en ellas podremos registrarnos, logearnos e incluso validar la cuenta.1
Este proyecto ha sido creado en menos de una semana, siguiendo buenas prácticas y todas las recomendaciones de Google.

## Características del proyecto:

- Arquitectura MVVM.
- Inyección de dependencias con *Dagger Hilt*
- Firebase (Tanto para login como para persistir en base de datos *realTime*)
- Splash optimizado
- Vistas reutilizables y TODO creado con *Constraint Layout* para optimizar los recursos de la UI
- Diseños y estilos creados para la reutilización por todo el proyecto

## Pantalla de introducción
La primera pantalla nos servirá de navegador hacia el login o el registro. Como curiosidad el *splash* está optimizado para evitar el típico *delay* inicial en blanco.
TODAS las navegaciones usar un *wrapper* event para optimizar más el livedata en eventos de una única acción.

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="./docs/introductionview.png">
</p>

## Pantalla de login

Aquí podremos iniciar sesión. Además comprobará si la cuenta ha sido verificada para acceder al supuesto detalle (no pedido en la prueba) o a la pantalla de verificación.

El control de errores se ejecuta de dos formas distintas. En tiempo real, cada vez que uno de los campos pierde el foco o se actualiza. Y una gestión secundaria por si el login no hace *match* en la base de datos.

Además la propia UI se actualiza reactivamente a través de su propio *StateFlow*.

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Tecsecurity-login.gif">
<img align="center" width="300" alt="portfolio_view" src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Tecsecurity-routes.gif">
</p>

## Pantalla de registro

En esta pantalla tendremos que rellenar un formulario con validaciones similares a la pantalla del login. Si todo es correcto nos registrará y además creará en una tabla de la base de datos de *Firebase* un documento para dicho usuario donde almacenará el *nickName*, nombre completo y email.

También dispone de un *scroll* ya que al ser tan larga puede acortarse en dispositivos pequeños.

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="./docs/signin.png">
</p>
