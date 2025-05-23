package ar.uba.fi.ingsoft1.football5.tasks;

import ar.uba.fi.ingsoft1.football5.projects.ProjectDTO;

record TaskDTO(
        long id,
        String title,
        String description,
        ProjectDTO project
) {
    TaskDTO(Task task) {
        this(task.getId(), task.getTitle(), task.getDescription(), new ProjectDTO(task.getProject()));
    }
}
