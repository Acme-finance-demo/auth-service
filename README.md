# auth-service

Session issuing and validation. Audit messages are rendered with Apache Commons Text's
`StringSubstitutor`, so that library is on a code path, not just in the manifest.

```
src/main/java/com/example/auth/
  AuthApp.java          entry point
  SessionService.java   issue / validate / revoke
  TokenTemplate.java    audit message rendering (StringSubstitutor)
src/test/java/          JUnit 5
```

`StringSubstitutor` is constructed with an explicit map. `StringSubstitutor.createInterpolator()`
is **not** used anywhere: the interpolating variant is what resolves `${script:…}`,
`${dns:…}` and `${url:…}`, which is the reachable path for CVE-2022-42889.

Dependency versions are deliberately old, so a scan has something to find.

```sh
./gradlew test
```
