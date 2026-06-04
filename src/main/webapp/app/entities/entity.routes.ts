import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'uniSchedulerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'faculty',
    data: { pageTitle: 'uniSchedulerApp.faculty.home.title' },
    loadChildren: () => import('./faculty/faculty.routes'),
  },
  {
    path: 'department',
    data: { pageTitle: 'uniSchedulerApp.department.home.title' },
    loadChildren: () => import('./department/department.routes'),
  },
  {
    path: 'building',
    data: { pageTitle: 'uniSchedulerApp.building.home.title' },
    loadChildren: () => import('./building/building.routes'),
  },
  {
    path: 'room',
    data: { pageTitle: 'uniSchedulerApp.room.home.title' },
    loadChildren: () => import('./room/room.routes'),
  },
  {
    path: 'professor',
    data: { pageTitle: 'uniSchedulerApp.professor.home.title' },
    loadChildren: () => import('./professor/professor.routes'),
  },
  {
    path: 'student-group',
    data: { pageTitle: 'uniSchedulerApp.studentGroup.home.title' },
    loadChildren: () => import('./student-group/student-group.routes'),
  },
  {
    path: 'course',
    data: { pageTitle: 'uniSchedulerApp.course.home.title' },
    loadChildren: () => import('./course/course.routes'),
  },
  {
    path: 'course-event',
    data: { pageTitle: 'uniSchedulerApp.courseEvent.home.title' },
    loadChildren: () => import('./course-event/course-event.routes'),
  },
  {
    path: 'timeslot',
    data: { pageTitle: 'uniSchedulerApp.timeslot.home.title' },
    loadChildren: () => import('./timeslot/timeslot.routes'),
  },
  {
    path: 'professor-preference',
    data: { pageTitle: 'uniSchedulerApp.professorPreference.home.title' },
    loadChildren: () => import('./professor-preference/professor-preference.routes'),
  },
  {
    path: 'solver-job',
    data: { pageTitle: 'uniSchedulerApp.solverJob.home.title' },
    loadChildren: () => import('./solver-job/solver-job.routes'),
  },
  {
    path: 'timetable',
    data: { pageTitle: 'uniSchedulerApp.timetable.home.title' },
    loadChildren: () => import('./timetable/timetable.routes'),
  },
  {
    path: 'timetable-version',
    data: { pageTitle: 'uniSchedulerApp.timetableVersion.home.title' },
    loadChildren: () => import('./timetable-version/timetable-version.routes'),
  },
  {
    path: 'timetable-entry',
    data: { pageTitle: 'uniSchedulerApp.timetableEntry.home.title' },
    loadChildren: () => import('./timetable-entry/timetable-entry.routes'),
  },
  {
    path: 'schedule-conflict',
    data: { pageTitle: 'uniSchedulerApp.scheduleConflict.home.title' },
    loadChildren: () => import('./schedule-conflict/schedule-conflict.routes'),
  },
  {
    path: 'exam',
    data: { pageTitle: 'uniSchedulerApp.exam.home.title' },
    loadChildren: () => import('./exam/exam.routes'),
  },
  {
    path: 'exam-schedule-entry',
    data: { pageTitle: 'uniSchedulerApp.examScheduleEntry.home.title' },
    loadChildren: () => import('./exam-schedule-entry/exam-schedule-entry.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
