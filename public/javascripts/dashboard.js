

function changeSubStatus(subTaskId, currentStatus) {
  const newStatus = currentStatus === 'completed' ? 'pending' : 'completed';

  fetch('/api/changeSubStatus', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      subTaskId: subTaskId,
      status: newStatus
    })
  })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          // Update the checkbox status if needed
          const checkbox = document.querySelector(`.task-checkbox[data-task-id="${subTaskId}"]`);
          if (checkbox) {
            checkbox.checked = newStatus === 'completed';
          }
        } else {
          alert('Failed to update sub-task status');
        }
      })
      .catch(error => console.error('Error:', error));
}

$(document).ready(function() {
  $('.task-checkbox').each(function() {
    var completed = $(this).data('completed');
    if (completed === 'completed') {
      $(this).prop('checked', true);
    }
  });
});

$(document).ready(function() {
  $('.task-checkbox').each(function() {
    var completed = $(this).data('completed');
    if (completed === 'completed') {
      $(this).prop('checked', true);
    }
  });
});
function createColabTask() {
  const taskTitle = document.querySelector('#colabTaskForm input[type="text"]').value;
  const subtaskInputs = document.querySelectorAll('#subtasksContainer input[type="text"]');
  const subtasks = Array.from(subtaskInputs).map(input => input.value).filter(value => value.trim() !== '');
  const selectedFriends = Array.from(document.querySelectorAll('.friend-box')).map(box => box.textContent.trim().split(' ')[0]);

  // Add logic to fetch selected collaborators if needed

  fetch('/api/createColabTask', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      task: taskTitle,
      subtasks: subtasks,
      collaborators: selectedFriends
    })
  })
      .then(response => {
        if (response.ok) {
          return response.json();
        }
        throw new Error('Network response was not ok.');
      })
      .then(data => {
        console.log('Colab task created successfully:', data);
        // Optionally handle success UI updates or navigation
        closeColabForm(); // Close the modal after successful creation
      })
      .catch(error => {
        console.error('Error creating colab task:', error);
        // Optionally handle error UI updates
      });
}


let subtaskCounter = 1;

function addSubtask() {
  subtaskCounter++;
  const subtasksContainer = document.getElementById('subtasksContainer');

  const subtaskDiv = document.createElement('div');
  subtaskDiv.className = 'subtask';
  subtaskDiv.id = `subtask-${subtaskCounter}`;

  const subtaskInput = document.createElement('input');
  subtaskInput.type = 'text';
  subtaskInput.placeholder = `Task ${subtaskCounter}`;

  const addButton = document.createElement('button');
  addButton.type = 'button';
  addButton.innerText = 'Add';
  addButton.onclick = addSubtask;

  const removeButton = document.createElement('button');
  removeButton.type = 'button';
  removeButton.innerText = 'Remove';
  removeButton.onclick = () => removeSubtask(subtaskDiv.id);

  subtaskDiv.appendChild(subtaskInput);
  subtaskDiv.appendChild(addButton);
  subtaskDiv.appendChild(removeButton);

  subtasksContainer.appendChild(subtaskDiv);
}

function removeSubtask(id) {
  const subtaskDiv = document.getElementById(id);
  subtaskDiv.remove();
  subtaskCounter--;
}


document.addEventListener("DOMContentLoaded", function() {
  var dropdown = document.getElementById("friendDropdown");
  var selectedFriendsContainer = document.getElementById("selectedFriendsContainer");

  dropdown.addEventListener("change", function() {
    var selectedOptions = Array.from(dropdown.selectedOptions);
    selectedOptions.forEach(option => addFriend(option.value, option.text));
    dropdown.value = null;
  });

  function addFriend(value, text) {
    if (!document.getElementById("friend-" + value)) {
      var friendBox = document.createElement("div");
      friendBox.className = "friend-box";
      friendBox.id = "friend-" + value;
      friendBox.innerHTML = text + ' <button class="remove-btn" onclick="removeFriend(\'' + value + '\')">&times;</button>';
      selectedFriendsContainer.appendChild(friendBox);
    }
  }

  window.removeFriend = function(value) {
    var friendBox = document.getElementById("friend-" + value);
    if (friendBox) {
      friendBox.remove();
    }
  }
});

function openColabForm() {
  var modal = document.getElementById("colabModal");
  modal.classList.add("show-colab-form");
}

function closeColabForm() {
  var modal = document.getElementById("colabModal");
  modal.classList.remove("show-colab-form");
}
function setupSidebarNavigation() {
  document.addEventListener('DOMContentLoaded', function() {
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    const allTasksSection = document.getElementById('all-tasks');
    const completedTasksSection = document.getElementById('completed-tasks');
    const deletedTasksSection = document.getElementById('deleted-tasks');

    const sections = [allTasksSection, completedTasksSection, deletedTasksSection];

    navLinks.forEach(link => {
      link.addEventListener('click', function(event) {
        event.preventDefault();

        const target = link.getAttribute('data-bs-target');


        if(target != "#charts-nav") {
          sections.forEach(section => {
            if (section.id === target.replace('#', '')) {
              section.classList.add('active');
              section.classList.remove('hidden');
            }
            else {
              section.classList.remove('active');
              section.classList.add('hidden');
              // setTimeout(() => {
              //   section.classList.add('hidden');
              // }, 500); // Match this duration to the CSS transition duration
            }
          });
        }
      });
    });
  });
}

// Call the function to initialize the sidebar navigation
setupSidebarNavigation();

function addTask(username) {
  const newTask = document.getElementById('newTask').value;
  if (newTask) {
    fetch('/api/addTask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        task: newTask,
        user: username
      })
    })
        .then(response => {
          if (response.ok) {
            // Reload the page to reflect the changes
            window.location.reload();
          } else {
            alert('Failed to add task');
          }
        })
        .catch(error => console.error('Error:', error));
  } else {
    alert('Task cannot be empty');
  }
}


document.addEventListener('DOMContentLoaded', function() {
  const inputs = document.querySelectorAll('.task-content');

  inputs.forEach(input => {
    input.dataset.originalValue = input.value;
    input.addEventListener('change', function(event) {
      updateTask(input);
    });
  });

  // Function to update task via API
  function updateTask(input) {
    var taskId = input.getAttribute('data-task-id');
    const updatedTask = {
      id: taskId,
      task: input.value,
    };

    if(input.value) {
      fetch(`/api/tasks/`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedTask)
      })
          .then(response => {
            if (response.ok) {
              console.log(`Task ${taskId} updated successfully`);
            } else {
              console.error(`Failed to update task ${taskId}`);
            }
          })
          .catch(error => console.error('Error:', error));
    } else{
      alert('Task cannot be empty');
      input.value = input.dataset.originalValue;
    }
  }
});



function deleteTask(taskId) {
  if (confirm("Are you sure you want to delete this task?")) {
    fetch('/api/deleteTask', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        id: taskId
      })
    })
        .then(response => {
          if (response.ok) {
            // Reload the page to reflect the changes
            window.location.reload();
          } else {
            alert('Failed to delete task');
          }
        })
        .catch(error => console.error('Error:', error));
  }
}

function markCompleted(taskId) {
    fetch('/api/completeTask', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        id: taskId
      })
    })
        .then(response => {
          if (response.ok) {
            // Reload the page to reflect the changes
            window.location.reload();
          } else {
            alert('Failed to complete task');
          }
        })
        .catch(error => console.error('Error:', error));
}

function markNotCompleted(taskId) {
  fetch('/api/undoCompleteTask', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      id: taskId
    })
  })
      .then(response => {
        if (response.ok) {
          // Reload the page to reflect the changes
          window.location.reload();
        } else {
          alert('Failed to undo task');
        }
      })
      .catch(error => console.error('Error:', error));
}

function subDelete(taskId, subTaskId) {
  fetch('/api/subDelete', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      taskId: taskId,
      subTaskId: subTaskId
    })
  })
      .then(response => {
        if (response.ok) {
          // Reload the page to reflect the changes
          window.location.reload();
        } else {
          alert('Failed to complete task');
        }
      })
      .catch(error => console.error('Error:', error));
}


$(document).ready(function() {
  $('.task-checkbox').each(function() {
    var completed = $(this).data('completed');
    if (completed === 'completed') {
      $(this).prop('checked', true);
    }
  });
});



function restoreTask(taskId) {
  fetch('/api/restoreTask', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      id: taskId
    })
  })
      .then(response => {
        if (response.ok) {
          // Reload the page to reflect the changes
          window.location.reload();
        } else {
          alert('Failed to undo task');
        }
      })
      .catch(error => console.error('Error:', error));
}

function setupLogout() {
  document.getElementById('signout-link').addEventListener('click', function(event) {
    event.preventDefault(); // Prevent default link behavior

    // Perform logout action here
    logout();
  });

  function logout() {
    // Perform logout logic, such as clearing session storage or sending a logout request to the server
    // Example: Clear session storage
    sessionStorage.clear();

    fetch('/logout', {
      method: 'GET', // Assuming GET method for logout
      headers: {
        'Content-Type': 'application/json'
      }
    })
        .then(response => {
          if (response.ok) {
            console.log('Logged out successfully');
            // Redirect or handle UI update as needed after logout
          } else {
            console.error('Failed to log out');
          }
        })
        .catch(error => console.error('Error:', error));

    // Redirect to login page or perform any other post-logout actions
    window.location.href = '/'; // Replace with your login page URL
  }
}

document.addEventListener('DOMContentLoaded', setupLogout);

(function() {
  "use strict";

  /**
   * Easy selector helper function
   */
  const select = (el, all = false) => {
    el = el.trim()
    if (all) {
      return [...document.querySelectorAll(el)]
    } else {
      return document.querySelector(el)
    }
  }

  /**
   * Easy event listener function
   */
  const on = (type, el, listener, all = false) => {
    if (all) {
      select(el, all).forEach(e => e.addEventListener(type, listener))
    } else {
      select(el, all).addEventListener(type, listener)
    }
  }

  /**
   * Easy on scroll event listener 
   */
  const onscroll = (el, listener) => {
    el.addEventListener('scroll', listener)
  }

  /**
   * Sidebar toggle
   */
  if (select('.toggle-sidebar-btn')) {
    on('click', '.toggle-sidebar-btn', function(e) {
      select('body').classList.toggle('toggle-sidebar')
    })
  }

  /**
   * Search bar toggle
   */
  if (select('.search-bar-toggle')) {
    on('click', '.search-bar-toggle', function(e) {
      select('.search-bar').classList.toggle('search-bar-show')
    })
  }

  /**
   * Navbar links active state on scroll
   */
  let navbarlinks = select('#navbar .scrollto', true)
  const navbarlinksActive = () => {
    let position = window.scrollY + 200
    navbarlinks.forEach(navbarlink => {
      if (!navbarlink.hash) return
      let section = select(navbarlink.hash)
      if (!section) return
      if (position >= section.offsetTop && position <= (section.offsetTop + section.offsetHeight)) {
        navbarlink.classList.add('active')
      } else {
        navbarlink.classList.remove('active')
      }
    })
  }
  window.addEventListener('load', navbarlinksActive)
  onscroll(document, navbarlinksActive)

  /**
   * Toggle .header-scrolled class to #header when page is scrolled
   */
  let selectHeader = select('#header')
  if (selectHeader) {
    const headerScrolled = () => {
      if (window.scrollY > 100) {
        selectHeader.classList.add('header-scrolled')
      } else {
        selectHeader.classList.remove('header-scrolled')
      }
    }
    window.addEventListener('load', headerScrolled)
    onscroll(document, headerScrolled)
  }

  /**
   * Back to top button
   */
  let backtotop = select('.back-to-top')
  if (backtotop) {
    const toggleBacktotop = () => {
      if (window.scrollY > 100) {
        backtotop.classList.add('active')
      } else {
        backtotop.classList.remove('active')
      }
    }
    window.addEventListener('load', toggleBacktotop)
    onscroll(document, toggleBacktotop)
  }

  /**
   * Initiate tooltips
   */
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
  var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
  })

  /**
   * Initiate quill editors
   */
  if (select('.quill-editor-default')) {
    new Quill('.quill-editor-default', {
      theme: 'snow'
    });
  }

  if (select('.quill-editor-bubble')) {
    new Quill('.quill-editor-bubble', {
      theme: 'bubble'
    });
  }

  if (select('.quill-editor-full')) {
    new Quill(".quill-editor-full", {
      modules: {
        toolbar: [
          [{
            font: []
          }, {
            size: []
          }],
          ["bold", "italic", "underline", "strike"],
          [{
              color: []
            },
            {
              background: []
            }
          ],
          [{
              script: "super"
            },
            {
              script: "sub"
            }
          ],
          [{
              list: "ordered"
            },
            {
              list: "bullet"
            },
            {
              indent: "-1"
            },
            {
              indent: "+1"
            }
          ],
          ["direction", {
            align: []
          }],
          ["link", "image", "video"],
          ["clean"]
        ]
      },
      theme: "snow"
    });
  }

  /**
   * Initiate TinyMCE Editor
   */

  const useDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const isSmallScreen = window.matchMedia('(max-width: 1023.5px)').matches;

  tinymce.init({
    selector: 'textarea.tinymce-editor',
    plugins: 'preview importcss searchreplace autolink autosave save directionality code visualblocks visualchars fullscreen image link media codesample table charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help charmap quickbars emoticons accordion',
    editimage_cors_hosts: ['picsum.photos'],
    menubar: 'file edit view insert format tools table help',
    toolbar: "undo redo | accordion accordionremove | blocks fontfamily fontsize | bold italic underline strikethrough | align numlist bullist | link image | table media | lineheight outdent indent| forecolor backcolor removeformat | charmap emoticons | code fullscreen preview | save print | pagebreak anchor codesample | ltr rtl",
    autosave_ask_before_unload: true,
    autosave_interval: '30s',
    autosave_prefix: '{path}{query}-{id}-',
    autosave_restore_when_empty: false,
    autosave_retention: '2m',
    image_advtab: true,
    link_list: [{
        title: 'My page 1',
        value: 'https://www.tiny.cloud'
      },
      {
        title: 'My page 2',
        value: 'http://www.moxiecode.com'
      }
    ],
    image_list: [{
        title: 'My page 1',
        value: 'https://www.tiny.cloud'
      },
      {
        title: 'My page 2',
        value: 'http://www.moxiecode.com'
      }
    ],
    image_class_list: [{
        title: 'None',
        value: ''
      },
      {
        title: 'Some class',
        value: 'class-name'
      }
    ],
    importcss_append: true,
    file_picker_callback: (callback, value, meta) => {
      /* Provide file and text for the link dialog */
      if (meta.filetype === 'file') {
        callback('https://www.google.com/logos/google.jpg', {
          text: 'My text'
        });
      }

      /* Provide image and alt text for the image dialog */
      if (meta.filetype === 'image') {
        callback('https://www.google.com/logos/google.jpg', {
          alt: 'My alt text'
        });
      }

      /* Provide alternative source and posted for the media dialog */
      if (meta.filetype === 'media') {
        callback('movie.mp4', {
          source2: 'alt.ogg',
          poster: 'https://www.google.com/logos/google.jpg'
        });
      }
    },
    height: 600,
    image_caption: true,
    quickbars_selection_toolbar: 'bold italic | quicklink h2 h3 blockquote quickimage quicktable',
    noneditable_class: 'mceNonEditable',
    toolbar_mode: 'sliding',
    contextmenu: 'link image table',
    skin: useDarkMode ? 'oxide-dark' : 'oxide',
    content_css: useDarkMode ? 'dark' : 'default',
    content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:16px }'
  });

  /**
   * Initiate Bootstrap validation check
   */
  var needsValidation = document.querySelectorAll('.needs-validation')

  Array.prototype.slice.call(needsValidation)
    .forEach(function(form) {
      form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
        }

        form.classList.add('was-validated')
      }, false)
    })

  /**
   * Initiate Datatables
   */
  const datatables = select('.datatable', true)
  datatables.forEach(datatable => {
    new simpleDatatables.DataTable(datatable, {
      perPageSelect: [5, 10, 15, ["All", -1]],
      columns: [{
          select: 2,
          sortSequence: ["desc", "asc"]
        },
        {
          select: 3,
          sortSequence: ["desc"]
        },
        {
          select: 4,
          cellClass: "green",
          headerClass: "red"
        }
      ]
    });
  })

  /**
   * Autoresize echart charts
   */
  const mainContainer = select('#main');
  if (mainContainer) {
    setTimeout(() => {
      new ResizeObserver(function() {
        select('.echart', true).forEach(getEchart => {
          echarts.getInstanceByDom(getEchart).resize();
        })
      }).observe(mainContainer);
    }, 200);
  }

})();