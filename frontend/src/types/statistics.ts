export interface AdminOverview {
  totalStudents: number; totalCoaches: number; totalVehicles: number
  todayReservations: number; todayCompleted: number; todayAbsent: number
  todaySchedules: number; todayAvailableSlots: number
  adjustmentCount: number; adjustmentRate: number; completedTrainingCount: number
}
export interface CoachLoad { coachId: number; coachName: string; scheduleCount: number; reservationCount: number; completedCount: number; capacity: number; usedCapacity: number; utilizationRate: number }
export interface ReservationTrend { date: string; reservationCount: number; completedCount: number; cancelledCount: number; absentCount: number }
export interface ScheduleUtilization { date: string; totalCapacity: number; usedCapacity: number; remainCapacity: number; utilizationRate: number }
export interface StudentProgress { rangeName: string; studentCount: number }
export interface RecentActivity { type: string; title: string; description: string; time: string }
export interface CoachOverview { todayReservations: number; todayCompleted: number; todayAbsent: number; upcomingReservations: number; completedTrainingCount: number; adjustmentReceivedCount: number }
export interface StudentOverview { completedTrainingCount: number; successReservationCount: number; cancelledReservationCount: number; absentCount: number; adjustmentCount: number; nextReservation: string; recentTrainingRecord: string }
