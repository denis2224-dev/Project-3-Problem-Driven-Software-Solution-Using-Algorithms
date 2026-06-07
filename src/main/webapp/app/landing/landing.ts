import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface LandingItem {
  title: string;
  text: string;
}

@Component({
  selector: 'jhi-landing',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export default class Landing {
  readonly problems: LandingItem[] = [
    { title: 'Professor clashes', text: 'Avoid assigning the same professor to overlapping activities.' },
    { title: 'Room conflicts', text: 'Prevent double-booked rooms and capacity violations.' },
    { title: 'Student overlap', text: 'Keep mandatory events clear for each student group.' },
    { title: 'Idle gaps', text: 'Reduce fragmented timetables and wasted waiting time.' },
  ];

  readonly features: LandingItem[] = [
    { title: 'Academic Data Management', text: 'Maintain faculties, departments, rooms, courses, groups, events, and preferences.' },
    {
      title: 'Automated Timetable Generation',
      text: 'Run an asynchronous solver job and track progress until a timetable version is created.',
    },
    { title: 'Exam Scheduling', text: 'Preview conflict-free exam periods using graph coloring over shared student groups.' },
    { title: 'Conflict Detection', text: 'Model hard constraints such as professor, group, room, capacity, and equipment clashes.' },
    { title: 'Solver Statistics', text: 'Expose color count, AC-3 reductions, backtracks, runtime, and soft penalty score.' },
    { title: 'Timetable Visualization', text: 'Review generated schedules in a readable weekly grid.' },
  ];

  readonly values: LandingItem[] = [
    { title: 'Administrators', text: 'Save hours of manual planning and get repeatable scheduling runs.' },
    { title: 'Professors', text: 'Receive balanced teaching schedules that respect availability and preferences.' },
    { title: 'Students', text: 'Get cleaner timetables with fewer collisions and avoidable gaps.' },
    { title: 'Faculties', text: 'Use rooms more efficiently with transparent scheduling decisions.' },
  ];

  readonly workflow = ['Academic Data', 'Conflict Graph', 'Solver Execution', 'Generated Timetable'];
}
