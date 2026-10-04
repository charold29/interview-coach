// Interview Coach: browser-side state.
// The server keeps nothing, so the practice target and the recent-question
// history live in this browser only. Storage can be unavailable (private
// windows, blocked site data); every access is wrapped so the page still works.

(function () {
  "use strict";

  const PROFILE_KEY = "coach.profile.v1";
  const HISTORY_KEY = "coach.history.v1";
  const HISTORY_MAX = 20;
  const PROFILE_FIELDS = ["role", "targetLevel", "years", "stack", "feedbackLanguage", "accessCode"];

  const $ = (id) => document.getElementById(id);

  function load(key, fallback) {
    try {
      const raw = window.localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (e) {
      return fallback;
    }
  }

  function save(key, value) {
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (e) {
      // Storage unavailable: nothing to persist, the page keeps working.
    }
  }

  // --- Practice target ------------------------------------------------------

  function restoreProfile() {
    const profile = load(PROFILE_KEY, {});
    PROFILE_FIELDS.forEach((name) => {
      const field = $(name);
      if (field && typeof profile[name] === "string" && profile[name] !== "") {
        field.value = profile[name];
      }
    });
    // First visit: open the target panel so people see what they can set.
    if (!profile.role) {
      $("target").open = true;
    }
  }

  function saveProfile() {
    const profile = {};
    PROFILE_FIELDS.forEach((name) => {
      const field = $(name);
      if (field) {
        profile[name] = field.value;
      }
    });
    save(PROFILE_KEY, profile);
  }

  function updateSummary() {
    const level = $("targetLevel");
    const levelLabel = level.options[level.selectedIndex].text;
    const role = $("role").value.trim() || "Backend Engineer";
    const years = $("years").value.trim();
    const stack = $("stack").value.trim();

    let text = levelLabel + " " + role;
    if (years) {
      text += ", " + years + (years === "1" ? " year" : " years");
    }
    if (stack) {
      text += ", " + stack;
    }
    $("target-summary").textContent = text;
  }

  // --- History --------------------------------------------------------------

  function renderHistory() {
    const items = load(HISTORY_KEY, []);
    const list = $("history-list");
    list.replaceChildren();
    items.forEach((item) => {
      const li = document.createElement("li");
      const button = document.createElement("button");
      button.type = "button";
      button.className = "history-item";
      button.title = "Practice this question again";

      const question = document.createElement("span");
      question.textContent = item.question;
      const score = document.createElement("span");
      score.className = "history-score";
      score.textContent = item.score + " " + item.level;

      button.append(question, score);
      button.addEventListener("click", () => useQuestion(item.question));
      li.append(button);
      list.append(li);
    });
    $("history").hidden = items.length === 0;
  }

  function remember(scorecard) {
    const entry = {
      question: scorecard.dataset.question,
      score: scorecard.dataset.score,
      level: scorecard.dataset.level,
      at: new Date().toISOString(),
    };
    const items = load(HISTORY_KEY, []).filter((item) => item.question !== entry.question);
    items.unshift(entry);
    save(HISTORY_KEY, items.slice(0, HISTORY_MAX));
    renderHistory();
  }

  function useQuestion(text) {
    $("question").value = text;
    $("answer").value = "";
    $("answer").focus();
    $("question").scrollIntoView({ behavior: "smooth", block: "center" });
  }

  // --- Wiring ---------------------------------------------------------------

  document.addEventListener("DOMContentLoaded", () => {
    restoreProfile();
    updateSummary();
    renderHistory();

    PROFILE_FIELDS.forEach((name) => {
      const field = $(name);
      if (field) {
        field.addEventListener("input", () => {
          saveProfile();
          updateSummary();
        });
        field.addEventListener("change", () => {
          saveProfile();
          updateSummary();
        });
      }
    });

    $("history-clear").addEventListener("click", () => {
      save(HISTORY_KEY, []);
      renderHistory();
    });

    // "Practice this one" on the follow-up question.
    $("result").addEventListener("click", (event) => {
      const button = event.target.closest("[data-use-followup]");
      if (button) {
        useQuestion(button.dataset.useFollowup);
      }
    });

    // Ctrl/Cmd + Enter submits from the answer box.
    $("answer").addEventListener("keydown", (event) => {
      if (event.key === "Enter" && (event.ctrlKey || event.metaKey)) {
        event.preventDefault();
        $("practice").requestSubmit();
      }
    });

    document.body.addEventListener("htmx:afterSwap", (event) => {
      if (event.detail.target.id !== "result") {
        return;
      }
      const scorecard = event.detail.target.querySelector(".scorecard");
      if (scorecard) {
        remember(scorecard);
      }
      if (window.matchMedia("(max-width: 959px)").matches) {
        event.detail.target.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    });
  });
})();
