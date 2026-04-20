/* ── OUSL Dispatch Mate — app.js ────────────────────────────── */

/* Auto-dismiss flash banners after 5 seconds */
document.addEventListener('DOMContentLoaded', function () {

  /* Flash banners fade out */
  document.querySelectorAll('.alert').forEach(function (el) {
    setTimeout(function () {
      el.style.transition = 'opacity .6s';
      el.style.opacity = '0';
      setTimeout(function () { el.remove(); }, 600);
    }, 5000);
  });

  /* Slot radio — highlight selected card */
  document.querySelectorAll('.slot-option input[type="radio"]').forEach(function (radio) {
    radio.addEventListener('change', function () {
      document.querySelectorAll('.slot-option').forEach(function (opt) {
        opt.classList.remove('selected');
      });
      if (this.checked) {
        this.closest('.slot-option').classList.add('selected');
      }
    });
    /* Mark already-checked on page load */
    if (radio.checked) radio.closest('.slot-option').classList.add('selected');
  });

  /* Token string — auto-uppercase as user types */
  var tokenInput = document.getElementById('tokenInput');
  if (tokenInput) {
    tokenInput.addEventListener('input', function () {
      var pos = this.selectionStart;
      this.value = this.value.toUpperCase();
      this.setSelectionRange(pos, pos);
    });
  }

  /* Confirm dialogs on destructive actions */
  document.querySelectorAll('[data-confirm]').forEach(function (el) {
    el.addEventListener('click', function (e) {
      if (!confirm(this.dataset.confirm)) e.preventDefault();
    });
  });

  /* Search — clear button shows when input has value */
  var searchInput = document.querySelector('.search-input');
  if (searchInput) {
    var clearBtn = document.querySelector('.search-clear');
    function toggleClear() {
      if (clearBtn) clearBtn.style.display = searchInput.value ? 'inline-flex' : 'none';
    }
    searchInput.addEventListener('input', toggleClear);
    toggleClear();
  }

  /* Mark active nav link */
  var path = window.location.pathname;
  document.querySelectorAll('.sidebar-nav a').forEach(function (a) {
    if (a.getAttribute('href') === path ||
        (path.startsWith(a.getAttribute('href')) && a.getAttribute('href') !== '/')) {
      a.classList.add('active');
    }
  });
});

/* Login tab switch */
function switchTab(t) {
  ['student', 'admin'].forEach(function (name) {
    var panel = document.getElementById('panel-' + name);
    var tab   = document.getElementById('tab-' + name);
    if (panel) panel.classList.toggle('hidden', name !== t);
    if (tab)   tab.classList.toggle('active',   name === t);
  });
}
