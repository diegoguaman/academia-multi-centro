# Notas Importantes - Día 4

## ⚠️ Scalars Personalizados

Los scalars personalizados (`DateTimeScalar` y `BigDecimalScalar`) pueden necesitar ajustes al compilar el proyecto, dependiendo de la versión exacta de `graphql-java` que Spring Boot GraphQL use internamente.

### Si encuentras errores de compilación:

1. **Opción 1: Usar tipos simples en el schema**
   - Cambiar `DateTime` a `String` en el schema
   - Convertir manualmente en los resolvers

2. **Opción 2: Usar librería graphql-java-extended-scalars**
   Agregar dependencia:
   ```xml
   <dependency>
       <groupId>com.graphql-java</groupId>
       <artifactId>graphql-java-extended-scalars</artifactId>
       <version>21.0</version>
   </dependency>
   ```
   Y usar scalars predefinidos.

3. **Opción 3: Ajustar implementación de Coercing**
   La interfaz `Coercing` puede variar según la versión. Revisa la documentación oficial de `graphql-java`.

### Estado Actual

Los scalars están implementados pero pueden requerir ajustes. Si el proyecto compila sin errores, están correctos. Si hay errores, sigue una de las opciones arriba.

---

## 📝 Resolvers Pendientes

Los resolvers de ejemplo (`MatriculaResolver` y `CursoResolver`) están implementados. Para las entidades restantes, sigue la guía en `02-guia-implementacion-graphql-entidades.md`.

---

## 🔐 Autenticación

Recuerda que GraphQL está protegido con JWT. El endpoint `/graphql` requiere autenticación. Envía el token en el header:

```
Authorization: Bearer <token>
```

---

## 🧪 Testing

Para probar GraphQL:

1. Obtén un token JWT desde `/api/auth/login`
2. Usa el token en las requests a `/graphql`
3. O usa GraphiQL si está habilitado

---

**Última actualización**: Día 4

