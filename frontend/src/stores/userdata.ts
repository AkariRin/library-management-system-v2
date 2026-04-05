import { defineStore } from 'pinia'

export const useUserdataStore = defineStore('userdata', {
  state: () => ({
    uuid: null as string | null,
    username: null as string | null,
    name: null as string | null,
    admin: false as boolean,
    isLoggedIn: false as boolean,
  }),
  getters: {
    isAuthenticated: (state) => state.isLoggedIn
  },
  actions: {
    login(uuid: string, username: string, name: string, admin: boolean) {
      this.uuid = uuid
      this.username = username
      this.name = name
      this.admin = admin
      this.isLoggedIn = true
    },
    logout() {
      this.uuid = null
      this.username = null
      this.name = null
      this.admin = false
      this.isLoggedIn = false
    },
    updateUsername(username: string) {
      this.username = username
    },
    updateName(name: string) {
      this.name = name
    },
  },
  persist: true,
})

