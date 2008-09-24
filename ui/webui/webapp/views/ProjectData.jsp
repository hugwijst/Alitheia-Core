<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.util.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
if (selectedProject.isValid()) {
%>                <div id="selectedproject">
<%
    // Indentation depth
    in = 11;

    // Will hold the main window's content
    StringBuilder b = new StringBuilder("");

    // Construct and setup this view
    ProjectDataView pdvRenderer = new ProjectDataView(selectedProject);
    pdvRenderer.setSettings(settings);
    pdvRenderer.setTerrier(terrier);

    // Assemble the view's content
    b.append(sp(in++) + "<table class=\"pdv\">\n");
    b.append(sp(in++) + "<tr>\n");

    Dimension dimension = new Dimension (new Long(350), null);
    // =======================================================================
    // Source code - overview window
    // =======================================================================
    b.append(sp(in++) + "<td class=\"pdv\">\n");
    Window winPrjCode = new Window(dimension);
    // Construct the window's title
    winPrjCode.setTitle("Source code overview");
    // Construct the window's content
    winPrjCode.setContent(pdvRenderer.getCodeInfo(in + 2));
    // Display the window
    b.append(winPrjCode.render(in));
    b.append(sp(--in) + "</td>\n");

    // =======================================================================
    // Developers and mailing lists - overview window
    // =======================================================================
    b.append(sp(in++) + "<td class=\"pdv\" style=\"padding-left: 5px;\">\n");
    Window winPrjDevs = new Window(dimension);
    // Construct the window's title
    winPrjDevs.setTitle("Developers and mailing lists");
    // Construct the window's content
    winPrjDevs.setContent(pdvRenderer.getDevsInfo(in + 2));
    // Display the window
    b.append(winPrjDevs.render(in));
    b.append(sp(--in) + "</td>\n");

    // =======================================================================
    // Bugs - overview window
    // =======================================================================
    b.append(sp(in++) + "<td class=\"pdv\" style=\"padding-left: 5px;\">\n");
    Window winPrjBugs = new Window(dimension);
    // Construct the window's title
    winPrjBugs.setTitle("Bugs overview");
    // Construct the window's content
    winPrjBugs.setContent(pdvRenderer.getBugsInfo(in + 2));
    // Display the window
    b.append(winPrjBugs.render(in));
    b.append(sp(--in) + "</td>\n");

    b.append(sp(--in) + "</tr>\n");
    b.append(sp(--in) + "</table>\n");

    // =======================================================================
    // Main window
    // =======================================================================
    // Instantiate the main window's widget
    Window winProjectData = new Window();
    // Construct the main window's title
    winProjectData.setTitle("Project: " + selectedProject.getName());
    // Construct the main window's title buttons
    icoCloseWin.setPath("/");
    icoCloseWin.setParameter("pid");
    icoCloseWin.setValue("none");
    winProjectData.addTitleIcon(icoCloseWin);
    // Construct the main window's content
    winProjectData.setContent(b.toString());
    // Display the main window
    out.print(winProjectData.render(in - 2));

%>                </div>
<%
}
%><!-- ProjectData.jsp -->
