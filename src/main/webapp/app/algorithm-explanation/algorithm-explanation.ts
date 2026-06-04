import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface AlgorithmCard {
  title: string;
  purpose: string;
  complexity: string;
  steps: string[];
}

@Component({
  selector: 'jhi-algorithm-explanation',
  templateUrl: './algorithm-explanation.html',
  styleUrl: './algorithm-explanation.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
})
export default class AlgorithmExplanation {
  readonly algorithms: AlgorithmCard[] = [
    {
      title: 'Welsh-Powell graph coloring',
      purpose: 'Builds a conflict graph and assigns initial timeslot colors so hard-clashing events do not start in the same slot.',
      complexity: 'O(V^2 + E) for degree ordering and greedy coloring on the conflict graph.',
      steps: [
        'Create one vertex for every course event or exam.',
        'Add an edge when two vertices share a professor, group, or room requirement conflict.',
        'Sort vertices by descending degree so the most constrained events are colored first.',
        'Assign the lowest available color that is not used by already-colored adjacent vertices.',
        'Use colors as a warm-start preference when building CSP domains.',
      ],
    },
    {
      title: 'Backtracking MAC with AC-3',
      purpose: 'Searches timeslot-room assignments while maintaining arc consistency and enforcing hard constraints.',
      complexity: 'Worst-case exponential O(d^n), reduced in practice by AC-3 pruning, MRV variable choice, and LCV value ordering.',
      steps: [
        'Build a CSP model P = (X, D, C), where variables are course events and values are timeslot-room tuples.',
        'Remove unary-invalid values for room capacity, equipment, room type, and unavailable professor times.',
        'Run AC-3 with a queue of arcs to delete unsupported values from neighboring domains.',
        'Use MRV to choose the variable with the smallest remaining domain.',
        'Use LCV to try values that eliminate the fewest choices for other variables.',
        'After each assignment, run MAC propagation before continuing the recursive search.',
      ],
    },
    {
      title: 'Soft constraint scoring',
      purpose: 'Evaluates valid schedules so the demo can explain quality, not only feasibility.',
      complexity: 'O(A log A) per professor/group/day after assignments are grouped by actor and time.',
      steps: [
        'Penalize idle gaps for student groups and professors.',
        'Penalize professor AVOID or UNAVAILABLE preference violations if present in a produced schedule.',
        'Penalize excessive building transitions between consecutive events.',
        'Report the score with backtracks, domain reductions, runtime, and hard conflict count.',
      ],
    },
  ];
}
