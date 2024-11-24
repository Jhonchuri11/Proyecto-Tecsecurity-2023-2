# Tecsecurity - [Proyecto 2023-II]

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Logo.jpg">
</p>

## Definición del proyecto:

El proyecto fue desarrollando para mostrar rutas en las calles, implementada con el framework Django, librería React y Kotlin.

## Características del proyecto:

- Firebase (Tanto para login y register de usuaurios)
- Diseños y estilos creados para la reutilización por todo el proyecto

## Pantalla de introducción
La primera pantalla nos servirá de navegador hacia el login o el registro.

<p align="center">
<img align="center" width="300" alt="portfolio_view" src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Index.jpg">
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

|                               Pantalla principal                               |                                   Resultado                                    |
|:------------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|
|  <img src="https://github.com/Jhonchuri11/GestorDocumental_Frontend_Busqueda/blob/master/Requerimientos/Index.jpg" style="height: 50%; width:50%;"/>  |  <img src="https://i.imgur.com/lH1NBas.png" style="height: 50%; width:50%;"/>  |

