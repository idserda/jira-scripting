package nl.topicus.jira

import com.atlassian.crowd.model.user.User
import com.atlassian.greenhopper.service.rapid.view.RapidViewService
import com.atlassian.greenhopper.service.sprint.SprintIssueService
import com.atlassian.greenhopper.service.sprint.SprintManager
import com.atlassian.greenhopper.service.sprint.SprintService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

// Agile board ID
def rapidBoardId = 504
def excludeTypes = ["Epic"]

@WithPlugin("com.pyxis.greenhopper.jira")

@JiraAgileBean
RapidViewService rapidViewService

@JiraAgileBean
SprintService sprintService

@JiraAgileBean
SprintIssueService sprintIssueService

@JiraAgileBean
SprintManager sprintManager

Issue issue = issue // provided in binding

def String issueTypeName = issue.getIssueTypeObject().getName()
if (issueTypeName in excludeTypes) {
    return
}

def User loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def view = rapidViewService.getRapidView(loggedInUser, rapidBoardId).getValue()

if (! view) {
    return
}

def sprintsInView = sprintManager.getSprintsForView(view).getValue()
def huidigeSprint = sprintsInView.find { it.active }

if (huidigeSprint) {
    sprintIssueService.addIssuesToSprint(loggedInUser, huidigeSprint, [issue])
}