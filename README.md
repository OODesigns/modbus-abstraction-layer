# device-hardware-comms
Protocol-neutral device communication layer with a plugin architecture for devices and transport adapters. Modbus TCP/RTU are the first protocol examples.

## Result model and value objects

Operations return `Response<T>` rather than exposing routine failures as exceptions. A successful response carries a value; a failed response carries non-blank details. `map` and `flatMap` pass a failure through unchanged, while asynchronous boundaries use `CompletableFuture<Response<T>>` and normalize transport exceptions into failure responses.

Raw configuration values enter the domain through validated value-object constructors. For example:

```java
var endpoint = new NetworkEndpoint(
	new IPAddress("192.0.2.10"),
	new Port(502),
	new Timeout(Duration.ofSeconds(2)));
```

Constructors reject invalid values immediately. APIs then accept the validated types rather than repeating raw numbers or strings. Modbus addresses and quantities enforce the protocol ranges documented in `TODO.md`.

## Provider setup

`CommunicationClientRegistry`, `DeviceFactory`, and `ResponseTransformerFactory` accept explicit providers or discover providers through `ServiceLoader`. The Modbus adapter exposes TCP and RTU factories; deployments choose a Modbus unit ID by constructing the corresponding factory with a `UnitId`.

Run the unit suite with:

```bash
./gradlew test
```

## Convert a PDF to Markdown

Create an isolated environment and install the converter dependency once:

```bash
python3 -m venv .venv
.venv/bin/python -m pip install -r tools/requirements.txt
```

Run the script without arguments to choose a PDF in a file picker, or pass a
PDF path directly. By default, the Markdown file is written beside the PDF
with the same name and a `.md` extension. Use `-o` to choose another output
path.

```bash
.venv/bin/python tools/pdf_to_markdown.py
.venv/bin/python tools/pdf_to_markdown.py path/to/document.pdf
.venv/bin/python tools/pdf_to_markdown.py path/to/document.pdf -o output/document.md
```
