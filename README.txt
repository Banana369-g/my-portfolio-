MY PORTFOLIO - HOW TO UPDATE IT
================================

Files
  index.html          the page
  style.css           the design
  script.js           project cards, preview/code viewer, uploads
  projects-data.js    made automatically by build-projects.ps1 (don't edit by hand)
  build-projects.ps1  run this after changing anything in "projects"
  images/profile.png  your profile photo
  projects/           one folder per web project
  old-version/        the previous index.html and style.css

Add your GitHub and social accounts
  Open links.js in VS Code, paste your links between the quotes, save,
  and reload index.html. Empty ones stay hidden.
  For GitHub, just your username is enough, e.g.  github: "yukesh123",

Change the profile photo (for everyone)
  Replace images/profile.png with a new photo (same file name).

Add a web project (for everyone)
  1. Copy the project folder into "projects" (keep its html, css, js and images together).
  2. Optional: add a project.json inside it, for example:
       { "title": "My Cafe Site", "description": "What it is and what I learned.",
         "tech": ["HTML", "CSS"], "entry": "index.html", "order": 1 }
     "entry" is the first page to show. "order" decides the position (1 = first).
  3. Right-click build-projects.ps1 > "Run with PowerShell".
  4. Reload index.html. The card, live preview and code are all there.
  To remove a project, delete its folder and run build-projects.ps1 again.

Java projects
  projects/java-teamwork01 and projects/java-lesson09 hold a COPY of the .java
  files from JAVA/Teamwork01 and JAVA/lesson09 (in their "src" folder), plus an
  index.html that shows them running in the browser.
  projects/java-teamwork04 holds a copy of JAVA/TEAMWORK04/src plus the shop
  pages the program generates (computerList.html, details/, orders.html,
  reviews.html). After running the Java app again, copy those pages over too.
  If you change the Java code, copy the new .java files into "src" again and
  run build-projects.ps1. Projects with "Java" in "tech" show under the Java filter.

Quick uploads in the browser (only you see them)
  Open index.html?edit  (add ?edit to the end of the address)
  - "Upload photo" changes your photo.
  - "Upload project" adds a project from a folder or files.
  These are saved only in this browser on this computer. Visitors and other
  computers will NOT see them. To make them permanent, use the steps above.
