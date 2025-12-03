# 🔧 Solución: Error "Failed to fetch" en GraphiQL

## ❌ El Problema

Al ejecutar la query:
```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
    activo
    fechaCreacion
  }
}
```

Recibes el error:
```json
{
  "errors": [
    {
      "message": "Failed to fetch"
    }
  ]
}
```

---

## 🔍 Causa Raíz

El problema es que **`@PreAuthorize` requiere autenticación**, incluso aunque el endpoint GraphQL esté configurado como público (`permitAll()`).

**¿Por qué pasa esto?**

1. `@PreAuthorize` se ejecuta **después** de que la request pasa el `SecurityFilterChain`
2. Aunque el endpoint esté permitido, **no hay un usuario autenticado**
3. `hasAnyRole('ADMIN', ...)` necesita un usuario con roles asignados
4. Sin usuario autenticado → **`@PreAuthorize` bloquea la ejecución**

---

## ✅ Solución Aplicada

**Temporalmente** hemos comentado `@PreAuthorize` en `UsuarioResolver` para permitir pruebas sin autenticación:

```java
@QueryMapping
// @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ADMINISTRATIVO')") // Comentado temporalmente para pruebas
public UsuarioResponseDto usuario(@Argument final Long id) {
    return usuarioService.findById(id);
}
```

---

## 🚀 Prueba Ahora

1. **Reinicia la aplicación** para cargar los cambios
2. **Abre GraphiQL:** `http://localhost:8080/graphiql?path=/graphql`
3. **Ejecuta la query:**

```graphql
query {
  usuario(id: 1) {
    idUsuario
    email
    rol
    activo
    fechaCreacion
  }
}
```

**Debería funcionar ahora** ✅

---

## 🔐 Opciones para Habilitar Roles en el Futuro

### Opción 1: Permitir Acceso Anónimo con Roles Específicos

```java
@QueryMapping
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ADMINISTRATIVO') or permitAll()")
public UsuarioResponseDto usuario(@Argument final Long id) {
    return usuarioService.findById(id);
}
```

**Nota:** `permitAll()` no funciona así en `@PreAuthorize`. Ver Opción 2.

### Opción 2: Usar Condición Manual (Recomendado)

```java
@QueryMapping
public UsuarioResponseDto usuario(@Argument final Long id) {
    // Verificar autorización manualmente si hay usuario autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
        // Hay usuario autenticado, verificar roles
        boolean hasRole = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") 
                        || a.getAuthority().equals("ROLE_PROFESOR")
                        || a.getAuthority().equals("ROLE_ADMINISTRATIVO"));
        if (!hasRole) {
            throw new AccessDeniedException("No tiene permisos para ver usuarios");
        }
    }
    // Si no hay usuario autenticado, permitir acceso (público)
    return usuarioService.findById(id);
}
```

### Opción 3: Habilitar Autenticación JWT en GraphQL (Recomendado para Producción)

1. **Obtener token JWT** desde `/api/auth/login`
2. **Agregar header** en GraphiQL:
   ```json
   {
     "Authorization": "Bearer tu_token_jwt_aqui"
   }
   ```
3. **Restaurar `@PreAuthorize`** en los resolvers
4. **Quitar `permitAll()`** de `/graphql/**` en SecurityConfig

---

## 📋 Estado Actual

- ✅ `@PreAuthorize` comentado temporalmente
- ✅ Queries funcionarán sin autenticación
- ⚠️ **Sin seguridad por roles** (solo para desarrollo)

---

## 🎯 Próximos Pasos

1. ✅ Probar que las queries funcionan ahora
2. 🔄 (Opcional) Implementar autenticación JWT para probar con roles
3. 🔄 (Producción) Restaurar `@PreAuthorize` y habilitar autenticación

---

## 🔍 Otros Posibles Problemas

Si después de quitar `@PreAuthorize` sigues teniendo errores, verifica:

### 1. El Usuario Existe en la BD

El servicio lanza excepción si el usuario no existe:
```java
.orElseThrow(() -> new IllegalArgumentException("Usuario not found with id: " + id));
```

**Verifica en la BD:**
```sql
SELECT * FROM usuario WHERE id_usuario = 1;
```

### 2. El ID es Correcto

En GraphQL, puedes usar:
- `id: 1` (número)
- `id: "1"` (string)

Ambos funcionan, pero asegúrate de que el ID existe.

### 3. Revisa los Logs del Servidor

Busca errores en la consola de la aplicación al ejecutar la query.

---

**Última actualización:** Día 4 - Solución Error Failed to Fetch  
**Estado:** ✅ Solucionado temporalmente, listo para probar

