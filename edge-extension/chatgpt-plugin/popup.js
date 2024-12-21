// Archivo popup.js

// Botón para extraer la respuesta
document.getElementById("extract").addEventListener("click", async () => {
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  chrome.scripting.executeScript(
    {
      target: { tabId: tab.id },
      function: extractChatGPTResponse,
    },
    (results) => {
      const response = results[0].result;
      document.getElementById("output").textContent = response;
      localStorage.setItem("chatgptResponse", response);
      document.getElementById("extract").style.display = "none";
      document.getElementById("send").style.display = "inline";
    }
  );
});

// Botón para enviar la respuesta extraída al endpoint
document.getElementById("send").addEventListener("click", async () => {
  const payload = localStorage.getItem("chatgptResponse");

  if (!payload) {
    document.getElementById("output").textContent = "Primero extrae una respuesta.";
    return;
  }

  document.getElementById("send").style.display = "none";
  document.getElementById("output").textContent = "Enviando...";

  try {
    const response = await fetch("http://localhost:8080/api/validation/validate", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ documentText: payload }),
    });

    if (!response.ok) throw new Error("Error en el servidor.");

    const result = await response.json();
    displayFormattedResponse(result.metrics);
  } catch (error) {
    document.getElementById("output").textContent = `Error: ${error.message}`;
  }
});


// Función para mostrar la respuesta formateada
function displayFormattedResponse(metrics) {
  const output = document.getElementById("output");
  output.innerHTML = `
    <div style="font-size: 1.6em; margin: 0; padding: 0;">
      <br>Tasa de &eacutexito:<b> ${metrics.searchSuccessRate}%</b>
      <br>Precisi&oacuten:<b> ${metrics.precisionRate.toFixed(2)}%</b>
      <br>Tiempo promedio de respuesta:<b> ${(metrics.averageResponseTime / 1e9).toFixed(2)} seg</b>
      <br>Puntaje de relevancia promedio:<b> ${metrics.averageRelevancyScore}</b>
    </div>
  `;
}


// Función para extraer la respuesta de ChatGPT
function extractChatGPTResponse() {
  const responseElement = document.querySelector(".markdown");
  return responseElement ? responseElement.innerText : "No se encontró la respuesta.";
}

// Estilos para los botones
document.addEventListener("DOMContentLoaded", () => {
  const buttons = document.querySelectorAll("button");
  buttons.forEach(button => {
    button.style.padding = "10px 20px";
    button.style.fontSize = "12px";
    button.style.borderRadius = "5px";
    button.style.border = "none";
    button.style.cursor = "pointer";
    button.style.margin = "5px";
  });

  document.getElementById("extract").style.backgroundColor = "#007bff";
  document.getElementById("extract").style.color = "#fff";

  document.getElementById("send").style.backgroundColor = "#28a745";
  document.getElementById("send").style.color = "#fff";
});

