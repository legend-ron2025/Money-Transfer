'use client';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
} from 'recharts';

const MONTHLY = [
  { month: 'Aug', income: 52000, expense: 38000 },
  { month: 'Sep', income: 52000, expense: 35000 },
  { month: 'Oct', income: 55000, expense: 41000 },
  { month: 'Nov', income: 52000, expense: 39000 },
  { month: 'Dec', income: 58000, expense: 44000 },
  { month: 'Jan', income: 52000, expense: 31240 },
];

const CATEGORIES = [
  { name: 'Food',          value: 8420,  color: '#f97316' },
  { name: 'Shopping',      value: 6200,  color: '#8b5cf6' },
  { name: 'Bills',         value: 5400,  color: '#ef4444' },
  { name: 'Transport',     value: 4200,  color: '#3b82f6' },
  { name: 'Entertainment', value: 3800,  color: '#ec4899' },
  { name: 'Other',         value: 3220,  color: '#6b7280' },
];

const TREND = [
  { date: 'Jan 5',  spent: 1200 }, { date: 'Jan 8',  spent: 3400 },
  { date: 'Jan 11', spent: 800  }, { date: 'Jan 14', spent: 5600 },
  { date: 'Jan 17', spent: 2100 }, { date: 'Jan 20', spent: 4200 },
  { date: 'Jan 23', spent: 1800 }, { date: 'Jan 26', spent: 6300 },
  { date: 'Jan 29', spent: 2900 },
];

const RECENT_TXN = [
  { icon: '🛒', name: 'BigBasket',    cat: 'Groceries',     amt: '−₹1,240', income: false },
  { icon: '🍕', name: 'Swiggy',       cat: 'Food Delivery', amt: '−₹380',   income: false },
  { icon: '💰', name: 'Salary',       cat: 'Income',        amt: '+₹52,000',income: true  },
  { icon: '📱', name: 'Jio Recharge', cat: 'Bills',         amt: '−₹299',   income: false },
  { icon: '🎬', name: 'Netflix',      cat: 'Streaming',     amt: '−₹649',   income: false },
];

function StatCard({ label, value, sub, valueColor = 'text-gray-900 dark:text-white' }: { label: string; value: string; sub?: string; valueColor?: string }) {
  return (
    <div className="card p-5">
      <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">{label}</p>
      <p className={`text-2xl font-extrabold ${valueColor}`}>{value}</p>
      {sub && <p className="text-xs text-gray-400 mt-1">{sub}</p>}
    </div>
  );
}

const fmt = (v: number) => `₹${v.toLocaleString('en-IN')}`;

export default function DashboardPage() {
  return (
    <div className="space-y-6 pb-8">

      {/* Beta notice */}
      <div className="rounded-xl bg-primary-50 dark:bg-primary-900/20 border border-primary-200 dark:border-primary-800/40 p-4 text-sm text-primary-800 dark:text-primary-300 flex gap-3">
        <span className="text-lg">ℹ️</span>
        <span>
          <strong>Web Dashboard (Beta)</strong> — showing sample data.
          Download the <a href="/download" className="underline font-semibold hover:text-primary-600">Android app</a> to connect your bank and see live data.
        </span>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Total Balance" value="₹84,520"  sub="+₹3,240 this month" />
        <StatCard label="Monthly Income"  value="₹52,000"  sub="This month" valueColor="text-green-600 dark:text-green-400" />
        <StatCard label="Monthly Expense" value="₹31,240"  sub="This month" valueColor="text-red-500 dark:text-red-400" />
        <StatCard label="Savings"         value="₹20,760"  sub="39.9% savings rate" valueColor="text-primary-600 dark:text-primary-400" />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* Income vs Expense bar chart */}
        <div className="lg:col-span-2 card p-6">
          <h2 className="font-semibold text-gray-900 dark:text-white mb-1">Income vs Expense</h2>
          <p className="text-xs text-gray-400 mb-5">Last 6 months</p>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={MONTHLY} barCategoryGap="35%" barGap={4}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#9ca3af' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} axisLine={false} tickLine={false} tickFormatter={v => `₹${v/1000}k`} />
              <Tooltip formatter={(v: number) => fmt(v)} contentStyle={{ borderRadius: '0.75rem', border: 'none', boxShadow: '0 4px 20px rgba(0,0,0,0.12)' }} />
              <Legend wrapperStyle={{ fontSize: 12 }} />
              <Bar dataKey="income"  name="Income"  fill="#4ade80" radius={[5,5,0,0]} />
              <Bar dataKey="expense" name="Expense" fill="#f87171" radius={[5,5,0,0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Category donut */}
        <div className="card p-6">
          <h2 className="font-semibold text-gray-900 dark:text-white mb-1">Spending Breakdown</h2>
          <p className="text-xs text-gray-400 mb-4">This month</p>
          <ResponsiveContainer width="100%" height={150}>
            <PieChart>
              <Pie data={CATEGORIES} cx="50%" cy="50%" innerRadius={40} outerRadius={68} paddingAngle={3} dataKey="value" startAngle={90} endAngle={-270}>
                {CATEGORIES.map((c, i) => <Cell key={i} fill={c.color} stroke="none" />)}
              </Pie>
              <Tooltip formatter={(v: number) => fmt(v)} contentStyle={{ borderRadius: '0.75rem', border: 'none', boxShadow: '0 4px 20px rgba(0,0,0,0.12)' }} />
            </PieChart>
          </ResponsiveContainer>
          <div className="space-y-2 mt-3">
            {CATEGORIES.map(c => (
              <div key={c.name} className="flex items-center justify-between text-xs">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ background: c.color }} />
                  <span className="text-gray-600 dark:text-gray-400">{c.name}</span>
                </div>
                <span className="font-semibold text-gray-800 dark:text-gray-200">{fmt(c.value)}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Spending trend line chart */}
      <div className="card p-6">
        <h2 className="font-semibold text-gray-900 dark:text-white mb-1">Daily Spending Trend</h2>
        <p className="text-xs text-gray-400 mb-5">January 2025</p>
        <ResponsiveContainer width="100%" height={180}>
          <LineChart data={TREND}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
            <XAxis dataKey="date" tick={{ fontSize: 11, fill: '#9ca3af' }} axisLine={false} tickLine={false} />
            <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} axisLine={false} tickLine={false} tickFormatter={v => `₹${v/1000}k`} />
            <Tooltip formatter={(v: number) => fmt(v)} contentStyle={{ borderRadius: '0.75rem', border: 'none', boxShadow: '0 4px 20px rgba(0,0,0,0.12)' }} />
            <Line type="monotone" dataKey="spent" stroke="#6c63ff" strokeWidth={2.5}
              dot={{ fill: '#6c63ff', r: 4, strokeWidth: 2, stroke: '#ffffff' }}
              activeDot={{ r: 6, fill: '#6c63ff', stroke: '#ffffff', strokeWidth: 2 }} />
          </LineChart>
        </ResponsiveContainer>
      </div>

      {/* Recent transactions */}
      <div className="card overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-50 dark:border-gray-800 flex items-center justify-between">
          <h2 className="font-semibold text-gray-900 dark:text-white">Recent Transactions</h2>
          <a href="/dashboard/transactions" className="text-xs text-primary-600 dark:text-primary-400 font-medium hover:underline">
            View all
          </a>
        </div>
        <div className="divide-y divide-gray-50 dark:divide-gray-800">
          {RECENT_TXN.map(t => (
            <div key={t.name + t.amt} className="px-6 py-4 flex items-center justify-between hover:bg-gray-50/50 dark:hover:bg-gray-800/30 transition-colors">
              <div className="flex items-center gap-4">
                <div className="w-10 h-10 rounded-xl bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-xl shrink-0">
                  {t.icon}
                </div>
                <div>
                  <p className="font-medium text-gray-900 dark:text-white text-sm">{t.name}</p>
                  <p className="text-xs text-gray-400">{t.cat}</p>
                </div>
              </div>
              <span className={`font-bold text-sm ${t.income ? 'text-green-600 dark:text-green-400' : 'text-red-500 dark:text-red-400'}`}>
                {t.amt}
              </span>
            </div>
          ))}
        </div>
      </div>

    </div>
  );
}
