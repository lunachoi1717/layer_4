import { ref } from 'vue'

const loginId = ref(localStorage.getItem('loginId') || null)
const userName = ref(localStorage.getItem('userName') || null)
const role = ref(localStorage.getItem('role') || null)
const grade = ref(localStorage.getItem('grade') || 'SAPPHIRE')
const isLoggedIn = ref(!!loginId.value)

export function useAuth() {
  function setLogin(id, name, userRole, userGrade) {
    loginId.value = id
    userName.value = name
    role.value = userRole
    grade.value = userGrade || 'SAPPHIRE'
    isLoggedIn.value = true
    localStorage.setItem('loginId', id)
    localStorage.setItem('userName', name || '')
    localStorage.setItem('role', userRole || 'ROLE_USER')
    localStorage.setItem('grade', userGrade || 'SAPPHIRE')
  }

  function clearLogin() {
    loginId.value = null
    userName.value = null
    role.value = null
    grade.value = 'SAPPHIRE'
    isLoggedIn.value = false
    localStorage.removeItem('loginId')
    localStorage.removeItem('userName')
    localStorage.removeItem('role')
    localStorage.removeItem('grade')
  }

  async function checkLoginStatus() {
    try {
      const res = await fetch('/v1/api/account/check')
      const data = await res.json()
      if (data.loggedIn) {
        setLogin(data.loginId, data.name, data.role, data.grade)
      } else {
        clearLogin()
      }
    } catch {
      clearLogin()
    }
  }

  const isAdmin = () => role.value === 'ROLE_ADMIN'

  return { loginId, userName, role, grade, isLoggedIn, isAdmin, setLogin, clearLogin, checkLoginStatus }
}
